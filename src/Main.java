import javax.swing.*;
import javax.swing.border.Border;
import java.io.IOException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;

public class Main implements ActionListener {

    // This class takes user inputs to complete tasks
    class userLayout extends JPanel{

        // Private text box and button
        private JTextField userInput;
        private JButton enterButton;

        // Initialises the text box and button
        public userLayout() {
            this.setLayout(new GridLayout(1, 2));
            userInput = new JTextField();
            enterButton = new JButton("Enter");
            this.add(userInput);
            this.add(enterButton);
        }

        // Getters for the text box and button, to collect the data
        public JTextField getUserInput() {
            return userInput;
        }
        public JButton getenterButton() {
            return enterButton;
        }
    }

    // This class set the letters on the grid after each guess
    class WordsLayout extends JPanel{
        // Array of size 5 to store the 5 letters words
        JLabel[] word = new JLabel[5];

        public WordsLayout() {
            // Layout for the grids where letters will be placed
            this.setLayout(new GridLayout(1,5));
            // Colour of the grid border to create the lines on the grid
            Border borderColour = BorderFactory.createLineBorder(Color.black);
            // Goes through each letter and grid
            for(int i = 0; i < 5; i++){
                word[i] = new JLabel();
                word[i].setHorizontalAlignment(JLabel.CENTER);
                // Set the label opacity
                word[i].setOpaque(true);
                // Sets border colour
                word[i].setBorder(borderColour);
                this.add(word[i]);
            }
        }

        public void clearWordsLayout() {
            for(int i = 0; i < 5; i++){
                word[i].setText("");
            }
        }

        // This puts the text entered on the panel and shows the color based on
        // the rules of game.
        public void setPanelWords(String charVal, Color color, int pos){
            this.word[pos].setText(charVal);
            this.word[pos].setBackground(color);
        }
    }

    // The wordle string that the user has to guess
    private String wordleString;
    // The number of guesses the user has had, once it exceeds 6 the game will end
    private int guesses = 0;
    // The game frame
    private JFrame wordleFrame;
    // Panel for the 6 guesses
    private WordsLayout[] WordsLayoutArray = new WordsLayout[6];
    // Methods can be used, like the enter button
    private userLayout userLayout;

    public Main(){
        // Title of the JFrame
        wordleFrame = new JFrame("Wordle Game");
        // Game frame size
        wordleFrame.setSize(425, 425);
        // Game will stop once you close the frame
        wordleFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        // Sets layout of the grids
        wordleFrame.setLayout(new GridLayout(10, 1));
        // Loads all components
        wordleFrame.setVisible(true);
        // Loads the interface at the centre of the screen
        wordleFrame.setLocationRelativeTo(null);

        // Text added to help the user understand some rules
        JLabel rule = new JLabel("Rules: " );
        JLabel rule1 = new JLabel("-Only 5 letter words");
        JLabel rule2 = new JLabel("-Only 6 guesses");

        // Text added to the frame
        wordleFrame.add(rule);
        wordleFrame.add(rule1);
        wordleFrame.add(rule2);

        // Makes array have only six guesses
        for(int i = 0; i < 6; i++){
            WordsLayoutArray[i] = new WordsLayout();
            // After a guess has been made it gets added to the grid
            wordleFrame.add(WordsLayoutArray[i]);
        }

        userLayout = new userLayout();
        // Action listener for when the button is pressed
        userLayout.getenterButton().addActionListener(this);
        wordleFrame.add(userLayout);
        // Makes sure components don't hide away
        wordleFrame.revalidate();

        // Displays the word to guess in the console
        wordleString = generateWord();
        System.out.println("Word to guess: " + wordleString);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Turn the word inputted by the user into a string
        String inputWord = this.userLayout.getUserInput().getText();

        // If guesses have exceeded 5 then it will terminate the program
        if(guesses > 5){
            JOptionPane.showMessageDialog(null, "Game over!", "You guessed incorrectly", JOptionPane.INFORMATION_MESSAGE);
            wordleFrame.dispose();
            return;
        }

        // Makes sure that the length of the word is 5
        if(inputWord.length() == 5){
            // if the word is the same then show message "you win"
            if(dictionaryString().contains(inputWord)) {
                if(wordToGuessEqual(inputWord.trim().toUpperCase())) {
                    clearAllPanels();
                    JOptionPane.showMessageDialog(null, "You Win!", "You guessed correctly", JOptionPane.INFORMATION_MESSAGE);
                    wordleFrame.dispose();
                    return;
                }
            }else{
                //if the word is not in the dictionary minus one guess for another chance
                guesses--;
                JOptionPane.showMessageDialog(null, "Word is not in the dictionary", "Whoops",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }else{
            //if the word is not the word length then minus one guess for another chance
            JOptionPane.showMessageDialog(null, "Incorrect Word Length!", "The word must be 5 letters long!", JOptionPane.INFORMATION_MESSAGE);
            guesses--;
        }
        guesses++;
    }

    // Clear grids
    private void clearAllPanels() {
        for(int i = 0; i<=guesses; i++) {
            WordsLayoutArray[i].clearWordsLayout();
        }
    }

    // This checks if the word inputted is equal to the word to guess
    private boolean wordToGuessEqual(String inputWord){
        // Compares each letter and assigns a colour
        List<String> listOfWords = Arrays.asList(wordleString.split(""));
        // Array for the Collection of the guesses
        String[] inputWordsArray = inputWord.split("");
        // Checks if letters match to the word to guess
        List<Boolean> letterMatch = new ArrayList<>();

        // Goes through the guessed word
        for(int i = 0; i < 5; i++){
            // Checks if the letters are contained in the word to guess
            if(listOfWords.contains(inputWordsArray[i])){
                if(listOfWords.get(i).equals(inputWordsArray[i])){
                    // If letter is in the correct position set grid colour to green
                    gridRow().setPanelWords(inputWordsArray[i], Color.GREEN, i);
                    // If letter matches set it to true
                    letterMatch.add(true);
                } else {
                    // If letter is not in the correct position but in the word set it to yellow
                    gridRow().setPanelWords(inputWordsArray[i], Color.YELLOW, i);
                    letterMatch.add(false);
                }
            } else {
                // If the letter is not in the word at all set the grid colour to grey
                gridRow().setPanelWords(inputWordsArray[i], Color.GRAY, i);
                letterMatch.add(false);
            }
        }
        return !letterMatch.contains(false);
    }

    // Correlates how many guesses to the row
    public WordsLayout gridRow() {
        return this.WordsLayoutArray[guesses];
    }

    // Reads from target words file and randomly selects a word for the user to guess
    public String generateWord(){
        Path path = Paths.get("targetWords.txt");
        List<String> wordList = new ArrayList<>();

        try {
            wordList = Files.readAllLines(path);
        }catch (IOException e){
            e.printStackTrace();
        }
        Random random = new Random();
        int pos = random.nextInt(wordList.size());
        return wordList.get(pos).toUpperCase(Locale.ROOT);
    }

    // Reads and returns the hashset of all words in the dictionary file
    public HashSet<String> dictionaryString() {
        HashSet<String> dict = new HashSet<>();

        try{
            Scanner d = new Scanner(new File("gameDictionary.txt"));
            while(d.hasNext()){
                dict.add(d.next());
            }
            d.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return dict;
    }

    public static void main(String[] args) {
        new Main();
    }
}