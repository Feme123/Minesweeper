package minesweeper

import java.util.*
import kotlin.random.Random

fun main() {
    val scanner = Scanner(System.`in`)

    print("How many mines do you want on the field? ")
    val mines = scanner.nextInt()

    val grilla = CharArray(81)
    val posicionMinas = mutableListOf<Int>()

    for (i in 0..80) {
        grilla[i] = '.'
    }

    for (i in 1..mines) {
        val pos = Random.nextInt(0, 81)
        if (!posicionMinas.contains(pos)) {
            posicionMinas.add(pos)
        }
    }

    var sigueJuego: Boolean
    var turno = 1
    do {
        imprimirTablero(grilla)
        sigueJuego = jugar(scanner, grilla, posicionMinas, turno)
        if (!sigueJuego) {
            break
        }
        turno++
    } while (!juegoGanado(grilla, posicionMinas))

    if (!sigueJuego) {
        for (i in grilla.indices) {
            if (posicionMinas.contains(i)) {
                grilla[i] = 'X'
            }
        }
        imprimirTablero(grilla)
        println("You stepped on a mine and failed!")
        return
    }
    imprimirTablero(grilla)

    println("Congratulations! You found all the mines!")

    scanner.close()
}

fun cantidadMinasAdyacentes(pos: Int, posicionMinas: List<Int>): Int {
    var qt = 0
    val x = (pos - pos % 9) / 9
    val y = pos % 9

    for (i in x - 1..x + 1) {
        for (j in y - 1..y + 1) {
            val aux = i * 9 + j
            if (i in 0..8 && j in 0..8 && aux != pos && posicionMinas.contains(aux)) {
                qt++
            }
        }
    }

    return qt
}

fun imprimirTablero(grilla: CharArray) {
    println(" |123456789|")
    println("-|---------|")
    for (i in 0..80) {
        if (i % 9 == 0) {
            print("${i / 9 + 1}|")
        }
        print("${grilla[i]}")
        if (i % 9 == 8) {
            println("|")
        }
    }
    println("-|---------|")
}

fun juegoGanado(grilla: CharArray, posicionMinas: List<Int>): Boolean {
    var qt = 0
    var correcto = true

    for (i in grilla.indices) {
        if (grilla[i] == '*') {
            qt++
            if (!posicionMinas.contains(i)) {
                correcto = false
                break
            }
        }
    }

    var qt2 = 0
    for (i in grilla.indices) {
        if (grilla[i] != '.' && grilla[i] != '*') {
            qt2++
        }
    }

    return (qt == posicionMinas.size && correcto) || qt2 == grilla.size - posicionMinas.size
}

fun jugar(scanner: Scanner, grilla: CharArray, posicionMinas: MutableList<Int>, turno: Int): Boolean {
    var x: Int
    var y: Int
    var option: String
    var pos: Int

    do {
        print("Set/unset mine marks or claim a cell as free: ")
        y = scanner.nextInt() - 1
        x = scanner.nextInt() - 1
        option = scanner.next()
        pos = x * 9 + y

        when (option) {
            "mine" -> {
                marcar(pos, grilla)
            }
            "free" -> {
                if (posicionMinas.contains(pos) && turno == 1) {
                    do {
                        val nuevaPos = Random.nextInt(0, 81)
                        var repetir = true
                        if (!posicionMinas.contains(nuevaPos)) {
                            posicionMinas[posicionMinas.indexOf(pos)] = nuevaPos
                            repetir = false
                        }
                    } while (repetir)
                } else if (posicionMinas.contains(pos)) {
                    return false
                }
                liberar(pos, grilla, posicionMinas)
            }
        }
    } while (grilla[pos].toInt() in 48..56 && option == "mine")
    return true
}

fun marcar(pos: Int, grilla: CharArray) {
    when {
        grilla[pos] == '.' -> {
            grilla[pos] = '*'
        }
        grilla[pos] == '*' -> {
            grilla[pos] = '.'
        }
        grilla[pos].toInt() in 48..56 -> {
            println("There is a number here!")
        }
    }
}

fun liberar(pos: Int, grilla: CharArray, posicionMinas: List<Int>) {
    if (pos < 0 || pos > 80 || grilla[pos].toInt() in 48..56 || grilla[pos] == '/' || posicionMinas.contains(pos)) {
        return
    }

    val minasAdyacentes = cantidadMinasAdyacentes(pos, posicionMinas)
    if (minasAdyacentes > 0) {
        grilla[pos] = (minasAdyacentes + 48).toChar()
        return
    }

    grilla[pos] = '/'

    liberar(pos - 10, grilla, posicionMinas)
    liberar(pos - 9, grilla, posicionMinas)
    liberar(pos - 8, grilla, posicionMinas)
    liberar(pos + 8, grilla, posicionMinas)
    liberar(pos + 9, grilla, posicionMinas)
    liberar(pos + 10, grilla, posicionMinas)
    liberar(pos - 1, grilla, posicionMinas)
    liberar(pos + 1, grilla, posicionMinas)
}
