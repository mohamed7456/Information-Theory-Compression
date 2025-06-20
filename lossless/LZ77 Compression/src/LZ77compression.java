import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern; 

public class LZ77compression {
    @SuppressWarnings("ConvertToTryWithResources")
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        OUTER:
        while (true) {
            System.out.println("1 for Compression, 2 for Decompression, 3 for Terminating the Program");
            int choice = sc.nextInt();
            sc.nextLine(); 
            switch (choice) {
                case 1 -> {
                    System.out.println("Enter the full path to the file you want to compress:");
                    String fileName = sc.nextLine();
                    String input = readFromFile(fileName);
                    String compressedString = compress(input);
                    createFile(fileName + "_compressed", compressedString);
                }
                case 2 -> {
                    System.out.println("Enter the full path to the file you want to decompress:");
                    String fileName = sc.nextLine();
                    String input = readFromFile(fileName);
                    String decompressedString = decompress(input);
                    createFile(fileName + "_decompressed", decompressedString);
                }
                case 3 ->{
                    System.out.println("Program Closed");
                    break OUTER;
                }
                default -> System.out.println("Invalid choice");
            }
        }
        sc.close(); 
    }


    private static String readFromFile(String fileName) {
        StringBuilder sb = new StringBuilder();
        File file = new File(fileName);
    
        if (!file.exists()) {
            System.out.println("File not found: " + file.getAbsolutePath());
            return "";
        }
        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                sb.append(sc.nextLine()).append("\n");
            }
        } catch (FileNotFoundException e) {
            System.out.println("Cannot read file: " + fileName);
        }
        return sb.toString().trim();
    }
    

    private static void createFile(String fileName, String content) {
        File file = new File(fileName + ".txt");
        try {
            file.createNewFile();
            try (java.io.FileWriter writer = new java.io.FileWriter(file)) {
                writer.write(content);
                System.out.println("File saved as " + file.getAbsolutePath());
            }
        } catch (IOException e) {
            System.out.println("Cannot write to file: " + fileName);
        }
    }
    

    public static class Tag {
        int position;
        int length;
        char nextChar;

        public Tag(int position, int length, char nextChar) {
            this.position = position;
            this.length = length;
            this.nextChar = nextChar;
        }
        public int getPosition() {
            return position;
        }
        public int getLength() {
            return length;
        }
        public char getNextChar() {
            return nextChar;
        }
        public String convertIntoString() {
            return String.format("<%d,%d,%c>", position, length, nextChar);
        }
    }

    public static String compress(String text) {
        String myString = "";
        List<Tag> tags = new ArrayList<>(); 

        for (int i = 0; i < text.length(); i++) {
            boolean found = false;
            int pos = 0;
            int length = 0;
            int j = Math.max(0, myString.length() - 5);
            while (j < myString.length()) {
                if (text.charAt(i) == myString.charAt(j)) {
                    found = true;
                    pos = myString.length() - j;
                    int k = 0;
                    while (i + k < text.length() && j + k < myString.length() && text.charAt(i + k) == myString.charAt(j + k)) {
                        k++;
                    }
                    length = k;
                    break;
                }
                j++;
            }
            char nextChar;
            if (i + length < text.length()) {
                nextChar = text.charAt(i + length);
            } else {
                nextChar = '\0';
            }
            
            int endIndex = i + length + 1;
            if (endIndex > text.length()) {
                endIndex = text.length();
            }
            myString += text.substring(i, endIndex);
            tags.add(new Tag(found ? pos : 0, length, nextChar));
            i += length;
        }
        
        String result = "";
        for (Tag tag : tags) {
            result += tag.convertIntoString();
        }        
        return result.trim();
    }

    public static List<Tag> convertIntoTags(String stringTags) {
        List<Tag> tags = new ArrayList<>();
        Pattern pattern = Pattern.compile("<(\\d+),(\\d+),(.?)>");
        Matcher matcher = pattern.matcher(stringTags);
        while (matcher.find()) {
            int position = Integer.parseInt(matcher.group(1));
            int length = Integer.parseInt(matcher.group(2));
            char nextChar = matcher.group(3).charAt(0);

            tags.add(new Tag(position, length, nextChar));
        }
        return tags;
    }

    public static String decompress(String text) {
        List<Tag> tags = convertIntoTags(text);
        String result = "";
        for (int im = 0; im < tags.size(); im++) {
            Tag tag = tags.get(im);
            int position = tag.getPosition();
            int length = tag.getLength();
            
            if (position > 0) {
                int startIndex = result.length() - position;
                result += result.substring(startIndex, startIndex + length);
            }
            result += tag.getNextChar();
        }
        return result.trim();
    }
}
