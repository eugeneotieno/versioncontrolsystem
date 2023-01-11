import java.io.File

const val COMMANDS = """These are SVCS commands:
config     Get and set a username.
add        Add a file to the index.
log        Show commit logs.
commit     Save changes.
checkout   Restore a file.
"""

fun main() {

    val workingDirectory = System.getProperty ("user.dir")
    val separator = File.separator
    val vcsDir = File("${workingDirectory}${separator}vcs")
    if (!vcsDir.exists()) vcsDir.mkdir()

    val configFile = vcsDir.resolve("config.txt")
    if (!configFile.exists()) configFile.createNewFile()

    val indexFile = vcsDir.resolve("index.txt")
    if (!indexFile.exists()) indexFile.createNewFile()

    print("Enter command or --help for commands: ")
    val cmdInput = readln().split(" ")
    when(cmdInput[0]) {
        "--help" -> println(COMMANDS)
        "config" -> {
            if (cmdInput.size > 1) {
                configFile.writeText("The username is ${cmdInput[1]}.")
                println(configFile.readText())
            } else {
                if (configFile.readText().isBlank()) {
                    println("Please, tell me who you are.")
                } else {
                    println(configFile.readText())
                }
            }
        }
        "add" -> {
            if (cmdInput.size > 1) {
                val trackFile = vcsDir.resolve(cmdInput[1])
                if (trackFile.exists()) {
                    if (indexFile.readText().isBlank()) {
                        indexFile.writeText(cmdInput[1])
                    } else {
                        var tracked = false
                        indexFile.forEachLine {
                            if (it == cmdInput[1]) tracked = true
                        }

                        if (!tracked) indexFile.appendText("\n${cmdInput[1]}")
                    }
                    println("The file '${cmdInput[1]}' is tracked.")
                } else {
                    println("Can't find '${cmdInput[1]}'.")
                }
            } else {
                if (indexFile.readText().isBlank()) {
                    println("Add a file to the index.")
                } else {
                    println("Tracked files:")
                    println(indexFile.readText())
                }
            }
        }
        "log" -> println("Show commit logs.")
        "commit" -> println("Save changes.")
        "checkout" -> println("Restore a file.")
        else -> println("'${cmdInput[0]}' is not a SVCS command.")
    }
}