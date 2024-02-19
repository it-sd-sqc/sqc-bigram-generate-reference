package edu.cvtc.bigram;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;
import java.sql.*;

@SuppressWarnings({"SpellCheckingInspection"})
class GenerateTest {
  @Test
  void main() {
    assertAll(
        () -> {
          ByteArrayOutputStream out = new ByteArrayOutputStream();
          System.setOut(new PrintStream(out));
          Generate.main(new String[]{"--version"});
          String output = out.toString();
          assertTrue(output.startsWith("Version "));
        },
        () -> {
          ByteArrayOutputStream out = new ByteArrayOutputStream();
          System.setOut(new PrintStream(out));
          Generate.main(new String[]{"--help"});
          String output = out.toString();
          assertTrue(output.startsWith("Generate a text"));
        },
        () -> assertDoesNotThrow(() -> {
          ByteArrayOutputStream out = new ByteArrayOutputStream();
          System.setErr(new PrintStream(out));
          Generate.main(new String[]{"--seed"});
          String output = out.toString();
          assertTrue(output.startsWith("Expected"));
        }),
        () -> assertDoesNotThrow(() -> {
          ByteArrayOutputStream out = new ByteArrayOutputStream();
          System.setOut(new PrintStream(out));
          Generate.main(new String[]{"--seed", "Generate"});
          String output = out.toString();
          assertTrue(output.startsWith("“Convince"));
        }),
        () -> assertDoesNotThrow(() -> {
          ByteArrayOutputStream out = new ByteArrayOutputStream();
          System.setOut(new PrintStream(out));
          Generate.main(new String[]{"--seed", "Generate"});
          String[] lines = out.toString().split(" ");
          assertTrue(lines.length == 100);
        })
      );
  }

  @Test
  void createConnection() {
    assertDoesNotThrow(
      () -> {
        Connection db = Generate.createConnection();
        assertNotNull(db);
        assertFalse(db.isClosed());
        db.close();
        assertTrue(db.isClosed());
      }
    );
  }

  // TODO: Create your test(s) below. /////////////////////////////////////////
}