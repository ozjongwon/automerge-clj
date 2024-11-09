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

def parse_java_file(java_filename: str) -> Optional[Tuple[str, dict[str, List[Tuple[str, List[str]]]]]]:
    """
    Parse Java source file and extract type name and method information.
    Handles both classes and interfaces.
    """
    try:
        with open(java_filename, 'r', encoding='utf-8') as file:
            java_source = file.read()

        tree = javalang.parse.parse(java_source)
        methods = {}
        type_name = None

        # Handle both ClassDeclaration and InterfaceDeclaration
        for path, node in tree.filter(javalang.tree.TypeDeclaration):
            if type_name is None:
                type_name = node.name

            # Process constructors only if it's a class
            if isinstance(node, javalang.tree.ClassDeclaration):
                for constructor in node.constructors:
                    if 'public' in constructor.modifiers:
                        params = []
                        for param in constructor.parameters:
                            param_type = format_type(param.type)
                            param_name = pascal_to_kebab(param.name)
                            params.append((param_type, param_name))
                        methods[type_name] = methods.get(type_name, []) + [['constructor', params]]

            # Process methods for both classes and interfaces
            for method in node.methods:
                if 'public' in method.modifiers:
                    method_name = method.name
                    return_type = 'nil' if method.return_type is None else format_type(method.return_type)

                    params = []
                    for param in method.parameters:
                        param_type = format_type(param.type)
                        param_name = pascal_to_kebab(param.name)
                        params.append((param_type, param_name))

                    methods[method_name] = methods.get(method_name, []) + [['method',params, return_type,
                                                                          'true' if 'static' in method.modifiers else 'false']]
        return (type_name, methods)

    except Exception as e:
        print(f"Error processing file: {e}")
        return None

def format_clojure_list(type_info: Tuple[str, dict]) -> str:
    """
    Format the parsed method information into Clojure-style lists.
    """
    type_name, methods = type_info
    lines = []

    lines.append(";; Auto-generated method definitions")
    lines.append(f";; From {type_name}.java\n")

    lines.append(f"\n:java-name => {type_name}\n")

    for original_name, mdefs in methods.items():
        for kind, params, *method_only in mdefs:
            if method_only:
                return_type, static_p = method_only
                param_str = ' '.join(f"[{param_type} {param_name}]" for param_type, param_name in params)
                formatted_name = f"{pascal_to_kebab(type_name)}-{pascal_to_kebab(original_name)}"

                # Add the original name and formatted definition
                lines.append(f"{original_name} => ({formatted_name} {return_type} ({param_str}) :static? {static_p})")
            else:
                # Format constructor
                param_str = ' '.join(f"[{param_type} {param_name}]" for param_type, param_name in params)
                constructor_name = f"make-{pascal_to_kebab(type_name)}"

                # Add the original name and formatted definition
                lines.append(f"{type_name} => ({constructor_name} {type_name} ({param_str}) :constructor? true)")

    return '\n'.join(lines)

def process_java_file(java_filename: str) -> None:
    """
    Process a Java file and create corresponding Clojure output file.
    """
    base_name = os.path.splitext(os.path.basename(java_filename))[0]
    output_filename = f"/tmp/{base_name}.clj"

    type_info = parse_java_file(java_filename)

    if not type_info:
        print("No methods were extracted.")
        return

    try:
        clojure_content = format_clojure_list(type_info)
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
