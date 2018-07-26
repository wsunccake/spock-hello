package com.mycls

import org.spockframework.mock.TooFewInvocationsError
import org.spockframework.mock.WrongInvocationOrderError
import spock.lang.FailsWith
import spock.lang.Specification

class UserControllerSpec extends Specification {

    UserController userController
    UserService userService

    def setup() {
        userService = Mock()
        userController = new UserController(
                userService: userService
        )
    }

    def 'use method arguments in return value'() {
        given:
        String email = "kitty@email.com"
        String name = "Hello Kitty"

        when:
        userController.createUser(email, name)

        then:
        1 * userService.createUser(email, name) >> { String e, String n -> new User(email: e, name: n)}
        1 * userService.sendWelcomeEmail({User u -> u.email == email && u.name == name})
    }

    def 'throw exception from mock method'() {
        given:
        String email = "test@email.com"
        String name = "John Doe"
        User user = new User()

        when:
        userController.createUser(email, name)

        then:
        1 * userService.createUser(email, name) >> user
        1 * userService.sendWelcomeEmail(user) >> { throw new IllegalStateException() }

        thrown(IllegalStateException)
    }

    def 'verify no other mock methods called'() {
        given:
        User user = new User()

        String email = "test@email.com"
        String name = "John Doe"

        when:
        userController.createUser(email, name)

        then:
        1 * userService.createUser(email, name) >> user
        1 * userService.sendWelcomeEmail(user)
        0 * _
    }

    def 'verify mock method call order - pass'() {
        given:
        User user = new User()

        String email = "test@email.com"
        String name = "John Doe"

        when:
        userController.createUser(email, name)

        then:
        1 * userService.createUser(email, name) >> user

        then:
        1 * userService.sendWelcomeEmail(user)
    }

    @FailsWith(WrongInvocationOrderError)
    def 'verify mock method call order - fail'() {
        given:
        User user = new User()

        String email = "test@email.com"
        String name = "John Doe"

        when:
        userController.createUser(email, name)

        then:
        1 * userService.sendWelcomeEmail(user)

        then:
        1 * userService.createUser(email, name) >> user
    }

    def 'mock definition outside of test spec'() {
        given:
        String email = "test@email.com"
        String name = "John Doe"

        when:
        userController.createUser(email, name)

        then:
        interaction {
            userCreationMocks(email, name)
        }
    }

    @FailsWith(TooFewInvocationsError)
    def 'mock definition outside of test spec - no interaction closure'() {
        given:
        String email = "test@email.com"
        String name = "John Doe"

        when:
        userController.createUser(email, name)

        then:
        userCreationMocks(email, name)
    }

    private void userCreationMocks(String email, String name) {
        User user = new User()

        1 * userService.createUser(email, name) >> user
        1 * userService.sendWelcomeEmail(user)
    }
    
}
