import javalang
from typing import List, Tuple, Optional
import os
import sys
import re

def pascal_to_kebab(name: str) -> str:
    """
    Convert PascalCase or camelCase to kebab-case.
    Example: getActorId -> get-actor-id
    """
    # Handle special cases where uppercase letters are together
    s1 = re.sub('(.)([A-Z][a-z]+)', r'\1-\2', name)
    s2 = re.sub('([a-z0-9])([A-Z])', r'\1-\2', s1)
    return s2.lower()

def format_type(java_type) -> str:
    """
    Format Java type into Clojure type notation, handling arrays.
    """
    if isinstance(java_type, javalang.tree.BasicType):
        base_type = java_type.name
    else:
        base_type = java_type.name

    # Check if it's an array type
    if hasattr(java_type, 'dimensions') and java_type.dimensions:
        return f"[:array-of {base_type}]"
    return base_type

def parse_java_file(java_filename: str) -> Optional[Tuple[str, List[Tuple[str, str, List[Tuple[str, str]]]]]]:
    """
    Parse Java source file and extract class name and method information.
    """
    try:
        with open(java_filename, 'r', encoding='utf-8') as file:
            java_source = file.read()

        tree = javalang.parse.parse(java_source)
        methods = []
        class_name = None

        # Walk through all class declarations
        for _, class_decl in tree.filter(javalang.tree.ClassDeclaration):
            class_name = class_decl.name

            # Process constructors
            for constructor in class_decl.constructors:
                if 'public' in constructor.modifiers:
                    params = []
                    for param in constructor.parameters:
                        param_type = format_type(param.type)
                        param_name = pascal_to_kebab(param.name)
                        params.append((param_type, param_name))
                    methods.append(('constructor', class_name, params))

            # Process regular methods
            for method in class_decl.methods:
                if 'public' in method.modifiers:
                    method_name = method.name
                    return_type = 'nil' if method.return_type is None else format_type(method.return_type)

                    params = []
                    for param in method.parameters:
                        param_type = format_type(param.type)
                        param_name = pascal_to_kebab(param.name)
                        params.append((param_type, param_name))

                    methods.append(('method', method_name, params, return_type))

        return (class_name, methods)

    except Exception as e:
        print(f"Error processing file: {e}")
        return None

def format_clojure_list(class_info: Tuple[str, List]) -> str:
    """
    Format the parsed method information into Clojure-style lists.
    """
    class_name, methods = class_info
    lines = []

    lines.append(";; Auto-generated method definitions")
    lines.append(f";; From {class_name}.java\n")

    lines.append(f"\n:class-name => {class_name}\n")

    for method in methods:
        if method[0] == 'constructor':
            # Format constructor
            _, _, params = method
            param_str = ' '.join(f"[{param_type} {param_name}]" for param_type, param_name in params)
            constructor_name = f"make-{pascal_to_kebab(class_name)}"

            # Add the original name and formatted definition
            lines.append(f"{class_name} => ({constructor_name} {class_name} ({param_str}))")

        else:
            # Format regular method
            _, original_name, params, return_type = method
            param_str = ' '.join(f"[{param_type} {param_name}]" for param_type, param_name in params)
            formatted_name = f"{pascal_to_kebab(class_name)}-{pascal_to_kebab(original_name)}"

            # Add the original name and formatted definition
            lines.append(f"{original_name} => ({formatted_name} {return_type} ({param_str}))")

    return '\n'.join(lines)

def process_java_file(java_filename: str) -> None:
    """
    Process a Java file and create corresponding Clojure output file.
    """
    base_name = os.path.splitext(os.path.basename(java_filename))[0]
    output_filename = f"/tmp/{base_name}.clj"

    class_info = parse_java_file(java_filename)
    if not class_info:
        print("No methods were extracted.")
        return

    try:
        clojure_content = format_clojure_list(class_info)
        with open(output_filename, 'w', encoding='utf-8') as file:
            file.write(clojure_content)
        print(f"Successfully created {output_filename}")

    except Exception as e:
        print(f"Error writing output file: {e}")

def main():
    if len(sys.argv) != 2:
        print("Usage: python script.py <java_file>")
        sys.exit(1)

    java_filename = sys.argv[1]
    process_java_file(java_filename)

if __name__ == "__main__":
    main()
