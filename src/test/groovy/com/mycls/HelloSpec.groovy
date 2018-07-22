package com.mycls

import spock.lang.Specification

class HelloSpec extends Specification {
    def "say hello" () {
        when:
        Hello hello = new Hello()

        then:
        hello.say(name) == words

        where:
        name    | words
        'Spock' | 'Hello Spock'
        'Java'  | 'Hello Java'
        'Groovy'| 'Hello Groovy'
    }

    def "say hello with expect"() {
        expect:
        new Hello().say(name) == words

        where:
        name    | words
        'Spock' | 'Hello Spock'
        'Java'  | 'Hello Java'
        'Groovy'| 'Hello Groovy'
    }
}
