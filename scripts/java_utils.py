import re


def remove_comments(content):
    """Remove comments from Java code."""
    # First remove multi-line comments
    content = re.sub(r"/\*.*?\*/", "", content, flags=re.DOTALL)

    # Then remove single-line comments
    result = []
    for line in content.split("\n"):
        # Find // that's not inside a string
        in_string = False
        comment_pos = -1
        for i, c in enumerate(line):
            if c == '"' and (i == 0 or line[i - 1] != "\\"):
                in_string = not in_string
            elif (
                c == "/" and i + 1 < len(line) and line[i + 1] == "/" and not in_string
            ):
                comment_pos = i
                break

        if comment_pos >= 0:
            result.append(line[:comment_pos])
        else:
            result.append(line)

    return "\n".join(result)


def is_valid_method_declaration(line):
    """Check if a line contains a valid method declaration."""
    # Skip obvious non-method lines
    stripped = line.strip()
    if not stripped or stripped.startswith("//") or stripped.startswith("*"):
        return False

    # Common Java control statements and operators to exclude
    non_methods = [
        "if",
        "while",
        "for",
        "switch",
        "catch",
        "!=",
        "==",
        "+=",
        "-=",
        "*=",
        "/=",
        "|=",
        "&=",
        "^=",
    ]

    # Must contain parentheses and opening brace
    if "(" not in line or ")" not in line or "{" not in line:
        return False

    # Check for common non-method patterns
    for pattern in non_methods:
        if pattern in line and line.find(pattern) < line.find("("):
            return False

    # Split the line up to the opening parenthesis
    before_paren = line[: line.find("(")].strip()
    words = [w for w in before_paren.split() if w]

    if len(words) < 2:  # Need at least return type and name
        return False

    # Check the last word (method name)
    method_name = words[-1]
    if not method_name.isidentifier():
        return False

    # Check if we have a return type
    modifiers = {
        "public",
        "private",
        "protected",
        "static",
        "final",
        "abstract",
        "synchronized",
        "native",
        "@Override",
    }

    # Look for return type among the words
    has_return_type = False
    for word in words[:-1]:
        if word not in modifiers:
            has_return_type = True
            break

    return has_return_type


def find_method_start(content, pos):
    """Find the next potential method start after pos."""
    lines = content[pos:].split("\n")
    cumulative_length = pos

    for line in lines:
        if is_valid_method_declaration(line):
            before_paren = line[: line.find("(")].strip()
            method_signature = line.strip()

            return cumulative_length, method_signature
        cumulative_length += len(line) + 1

    return -1, None


def find_matching_brace(content, start_pos):
    """Find the position of the matching closing brace."""
    brace_count = 0
    in_string = False
    in_char = False

    for i in range(start_pos, len(content)):
        c = content[i]

        # Handle strings and character literals
        if c == '"' and not in_char and (i == 0 or content[i - 1] != "\\"):
            in_string = not in_string
        elif c == "'" and not in_string and (i == 0 or content[i - 1] != "\\"):
            in_char = not in_char

        # Only count braces outside of strings and char literals
        if not in_string and not in_char:
            if c == "{":
                brace_count += 1
            elif c == "}":
                brace_count -= 1
                if brace_count == 0:
                    return i

    return -1


def count_code_lines(content):
    """Count non-empty, non-comment lines of code."""
    count = 0
    for line in content.split("\n"):
        stripped = line.strip()
        if stripped and not stripped.startswith("//"):
            count += 1
    return count


def analyze_java_file(content):
    """Analyze Java file and return method line counts."""
    # Remove all comments first
    content = remove_comments(content)

    methods = {}
    pos = 0

    while pos < len(content):
        # Find next method start
        pos, method_signature = find_method_start(content, pos)
        if pos == -1:
            break

        # Find position of opening brace
        brace_pos = content.find("{", pos)
        if brace_pos == -1:
            break

        # Find matching closing brace
        end_pos = find_matching_brace(content, brace_pos)
        if end_pos == -1:
            break

        # Count lines in method body
        method_body = content[brace_pos + 1 : end_pos]
        line_count = count_code_lines(method_body)

        if method_signature:
            methods[method_signature] = line_count

        pos = end_pos + 1

    return methods

