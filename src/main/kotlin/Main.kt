import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

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

    val logFile = vcsDir.resolve("log.txt")
    if (!logFile.exists()) logFile.createNewFile()

    val commitsDir = File("${workingDirectory}${separator}vcs${separator}commits")
    if (!commitsDir.exists()) commitsDir.mkdir()

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
        "log" -> {
            if (logFile.readText().isBlank()) {
                println("No commits yet.")
            } else {
                println(logFile.readText())
            }
        }
        "commit" -> {
            if (cmdInput.size > 1) {

                var content = ""
                val trackedFiles = vcsDir.parentFile.listFiles()
                for (file in trackedFiles!!) {
                    if (file.name != "untracked_file.txt") {
                        if (!file.isDirectory) {
                            content += file.readText()
                        }
                    }
                }

                val md = MessageDigest.getInstance("SHA-256")
                val messageDigest = md.digest(content.toByteArray())
                val no = BigInteger(1, messageDigest)
                var hashtext = no.toString(16)
                while (hashtext.length < 32) {
                    hashtext = "0$hashtext"
                }

                val hashDir = File("${workingDirectory}${separator}vcs${separator}commits${separator}${hashtext}")
                if (hashDir.exists()) {
                    println("Nothing to commit.")
                } else {
                    hashDir.mkdir()

                    for (file in trackedFiles) {
                        if (file.name != "untracked_file.txt") {
                            if (!file.isDirectory) {
                                val newFile = hashDir.resolve(file.name)
                                if (!newFile.exists()) newFile.createNewFile()
                                newFile.writeText(file.readText())
                            }
                        }
                    }

                    val logMsg = "commit $hashtext\nAuthor: ${configFile.readText().split(" ").last().replace(".", "")}\n${cmdInput[1]}"
                    if (logFile.readText().isBlank()) {
                        logFile.writeText(logMsg)
                    } else {
                        val prevCommits = logFile.readText()
                        logFile.writeText("$logMsg\n\n$prevCommits")
                    }

                    println("Changes are committed.")
                }
            } else {
                println("Message was not passed.")
            }
        }
        "checkout" -> {
            if (cmdInput.size > 1) {
                val commitDir = File("${workingDirectory}${separator}vcs${separator}commits${separator}${cmdInput[1]}")
                if (commitDir.exists()) {
                    for (file in commitDir.listFiles()!!) {
                        val newFile = vcsDir.parentFile.resolve(file.name)
                        if (!newFile.exists()) newFile.createNewFile()
                        newFile.writeText(file.readText())
                    }
                    println("Switched to commit ${cmdInput[1]}.")
                } else {
                    println("Commit does not exist.")
                }
            } else {
                println("Commit id was not passed.")
            }
        }
        else -> println("'${cmdInput[0]}' is not a SVCS command.")
    }
}