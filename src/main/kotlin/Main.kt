const val COMMANDS = """These are SVCS commands:
config     Get and set a username.
add        Add a file to the index.
log        Show commit logs.
commit     Save changes.
checkout   Restore a file.
"""

fun main(args: Array<String>) {
    print("Enter command or --help for commands: ")
    when(val cmd = readln()) {
        "--help" -> println(COMMANDS)
        "config" -> println("Get and set a username.")
        "add" -> println("Add a file to the index.")
        "log" -> println("Show commit logs.")
        "commit" -> println("Save changes.")
        "checkout" -> println("Restore a file.")
        else -> println("'$cmd' is not a SVCS command.")
    }
}