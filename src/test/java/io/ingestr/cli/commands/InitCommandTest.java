package io.ingestr.cli.commands;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InitCommandTest {

    @Test
    void shouldValidateName() {
        InitCommand ic = new InitCommand();
        assertTrue(ic.validateName("asdf"));
        assertTrue(ic.validateName("aasdfkjhASLFKJ123"));
        assertTrue(ic.validateName("a-_."));
        assertTrue(ic.validateName("a-_."));

        assertFalse(ic.validateName("$^^"));
        assertFalse(ic.validateName("*"));
        assertFalse(ic.validateName(" "));
        assertFalse(ic.validateName("asdf ffa"));
    }


    @Test
    void shouldValidateGroup() {
        InitCommand ic = new InitCommand();
        assertTrue(ic.validateGroup("asdf"));
        assertTrue(ic.validateGroup("io.test.domain"));
    }



}