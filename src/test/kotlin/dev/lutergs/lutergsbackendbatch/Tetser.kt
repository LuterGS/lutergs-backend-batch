package dev.lutergs.lutergsbackendbatch


class Test (

    test: Int
) {
    var test: String = test.toString()
        set(value) {
            field = "$value is value"
        }
}


class Person(email: String) {
    var email: String = email.required()
        set(value) {
            field = value.required()
        }

    private fun String.required() = this.also {
        require(it.trim().isNotEmpty()) {
            "The email cannot be blank"
        }
    }
}

fun tester() {
    Person("1")
}