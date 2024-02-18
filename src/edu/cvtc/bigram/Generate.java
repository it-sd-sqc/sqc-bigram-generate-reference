package edu.cvtc.bigram;

// Import statements //////////////////////////////////////////////////////////
import java.sql.*;
import java.text.MessageFormat;
import java.util.Random;

public class Generate {
    // Constants ////////////////////////////////////////////////////////////////
    public static final String DATABASE_NAME = "bigrams";
    public static final String DATABASE_PATH = DATABASE_NAME + ".db";

    private static final String DESCRIPTION = "Generate a text using the included bigram database.";

    private static final int TIMEOUT_STATEMENT_S = 5;
    private static final int VERSION = 1;

    public static class Word {
        public int id;
        public String string;

        public Word(int anId, String aString) {
            id = anId;
            string = aString;
        }
    }

    // Entry point //////////////////////////////////////////////////////////////
    public static void main(String[] args) {
        Random rng = new Random();
        int wordsLeft = 100;

        // Process arguments //////////////////////////////////////////////////////
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            switch (arg) {
                case "--version", "-v" -> {
                    System.out.println("Version " + VERSION);
                    return;
                }
                case "--help", "-h" -> {
                    System.out.println(DESCRIPTION);
                    System.out.println("""
              Usage:
                java edu.cvtc.bigram.Generate [length]
              Arguments:
                --version, -v
                  Displays the version number and exits.
                --help, -h
                  Displays argument help and exits.
                --seed string, -s string
                  Specify a seed for the random number generator
                length
                  The number of words to generate; defaults to 100.
              """);
                    return;
                }
                case "--seed", "-s" -> {
                    if (i + 1 >= args.length) {
                        System.err.println("Expected a string to use as a seed.");
                        return;
                    }
                    long seed = args[i + 1].hashCode();
                    rng.setSeed(seed);
                }
                default -> {
                    try {
                        wordsLeft = Integer.parseInt(arg);
                    } catch (Exception e) {
                        System.err.println("Expected an integer length: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            } // end switch
        } // end for

        Connection db = createConnection();
        StringBuilder result = new StringBuilder();
        try {
            Word w0 = getRandomWord(db, rng);
            result.append(w0.string);
            for (wordsLeft--; wordsLeft > 0; wordsLeft--) {
                Word w1 = getRandomBigram(db, rng, w0);
                result.append(' ');
                result.append(w1.string);
                w0 = w1;
            }
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        System.out.println(result.toString());
    }

    // Database methods /////////////////////////////////////////////////////////
    public static Connection createConnection() {
        Connection result = null;
        try {
            result = DriverManager.getConnection("jdbc:sqlite:" + DATABASE_PATH);
            System.out.println("Created connection: " + result);
            Statement command = result.createStatement();
            command.setQueryTimeout(TIMEOUT_STATEMENT_S);
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    //
    public static Word getRandomBigram(Connection db, Random rng, Word aWord)
        throws SQLException
    {
        int aNumber = rng.nextInt();

        Statement command = db.createStatement();
        ResultSet rows = command.executeQuery("""
            SELECT id, string FROM words
            WHERE id = (SELECT next_words_id FROM bigrams
                ORDER BY RANDOM() LIMIT 1)
            """);

        rows.next();
        Word aNewWord = new Word(rows.getInt("id"),
            rows.getString("string"));
        return aNewWord;
    }

    // Returns a Word to begin generating bigram pairs. The returned
    // word should be capitalized to resemble an English sentence.
    public static Word getRandomWord(Connection db, Random rng) throws SQLException {
        // nextInt can also return a negative number.
        int aNumber = Math.abs(rng.nextInt());

        Statement command = db.createStatement();
        ResultSet rows = command.executeQuery(
            "SELECT id, string FROM words WHERE id = "
                + aNumber
                + " % (SELECT COUNT(*) FROM words) + 1");

        rows.next();
        Word aWord = new Word(rows.getInt("id"),
            rows.getString("string"));
        return aWord;
    }
}