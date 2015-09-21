import java.io.*;
import java.util.*;

class SlidingWindow {

    // Declare a stream of input
    DataInputStream inStream;

    // Store the bytes of input file in a String
    static ArrayList<Character> fileArray = new ArrayList<Character>();

    // Our sliding window
    static ArrayList<Character> window = new ArrayList<Character>();

    // size of window
    // *** CAN BE CHANGED FOR LARGER FILES
    static final int WINDOW_SIZE = 30;

    // Store file sizes to see how much compression we get
    long inFileSize;
    long inFileSizeSave;
    long outFileSize;
    long decompressedOutFileSize;

    // Track how many bytes we've read. Useful for large files.
    int byteCount;

    // Other Integers
    int maxLength = 0;
    int tempStore = 0;

    // Boolean to Run Decompression
    static boolean runDeCompress = false;

    public void readFile(String fileName) {

        try {
            // Create a new File object, get size
            File inputFile = new File(fileName);
            inFileSize = inputFile.length();
            if (runDeCompress == false) {
            	inFileSizeSave = inFileSize;
            }
            // The constructor of DataInputStream requires an InputStream
            inStream = new DataInputStream(new FileInputStream(inputFile));
        }

        // Oops.  Errors.
        catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(0);
        }


        // Read the input file
        try {

            // While there are more bytes available to read...
            while (inStream.available() > 0) {

                // Read in a single byte and store it in a character
                char c = (char)inStream.readByte();

                if ((++byteCount)% 1024 == 0)
                    System.out.println("Read " + byteCount/1024 + " of " + inFileSize/1024 + " KB...");

                // Print the characters to see them for debugging purposes
                //System.out.print(c);

                // Add the character to an ArrayList
                fileArray.add(c);
            }

            // clean up
            inStream.close();
            System.out.println("Done!!!\n");
        }

        // Oops.  Errors.
        catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }

        // Print the ArrayList contents for debugging purposes
        // System.out.println(fileArray);
    }


    public void compressAndSaveAs(String fileName){
        // At this point, the ArrayList fileArray contains the file as a raw collection of Characters
        // Compress the input file by processing the ArrayList and writing the result to a new output file
        FileWriter fw = null;
        String firstWindowWrite = "";
        String tempString = "";
        String tempByte = "";
        for (int writeInit = 0; writeInit < WINDOW_SIZE; writeInit++){
            firstWindowWrite += fileArray.get(writeInit);
        }
        try {
            fw = new FileWriter(fileName, true);
            fw.write(firstWindowWrite, 0, firstWindowWrite.length());
            fw.close();
        } catch (IOException i){
            System.out.println("Error: " + i.getMessage());
        }

        // Start Compression Techniques
        for (int i = 0; i < fileArray.size() - WINDOW_SIZE; i++){
            for (int fillArray = i; fillArray < WINDOW_SIZE + i; fillArray++){
                window.add(fileArray.get(fillArray));
            }
            if (i != fileArray.size() - WINDOW_SIZE){

            } else {
                break;
            }
            for (int k = 0; k < WINDOW_SIZE; k++){
                int currentLength = 0;
                if ((k != WINDOW_SIZE) && (window.get(k) == fileArray.get(WINDOW_SIZE + i))){
                    if (k != WINDOW_SIZE){
                        currentLength++;
                        int nextChar = k + 1;
                        int outsideWindow = 1;
                        while ((nextChar != WINDOW_SIZE) && ((WINDOW_SIZE + i + outsideWindow) != fileArray.size()) && (window.get(nextChar) == 

fileArray.get(WINDOW_SIZE + i + outsideWindow))){
                            currentLength++;
                            nextChar++;
                            outsideWindow++;
                        }
                        if (currentLength > maxLength){
                            maxLength = currentLength;
                            tempStore = k;
                        }
                    }
                }
            }
            if (maxLength > 3){
                tempString = "" + (char)7 + "" + (char)tempStore + "" + (char)maxLength + "";
                try {
                    fw = new FileWriter(fileName, true);
                    fw.write(tempString, 0, tempString.length());
                    fw.close();
                } catch (IOException excep){
                    System.out.println("Error: " + excep.getMessage());
                }
            } else if (maxLength <= 3){
                tempByte = "" + fileArray.get(WINDOW_SIZE + i) + "";
                try {
                    fw = new FileWriter(fileName, true);
                    fw.write(tempByte, 0, tempByte.length());
                    fw.close();
                } catch (IOException io){
                    System.out.println("Error: " + io.getMessage());
                }
            }
            for (int removeArray = WINDOW_SIZE - 1; removeArray >= 0; removeArray--){
                window.remove(removeArray);
            }
            if (maxLength > 3){
                i += maxLength - 1;
            }
            maxLength = 0;
        }

        // Find Compression Percentage
        File outputFile = new File(fileName);
        outFileSize = outputFile.length();

        // Decompress Boolean Set
        runDeCompress = true;
        
        for (int removeFileArray = fileArray.size() - 1; removeFileArray >= 0; removeFileArray--) {
            fileArray.remove(removeFileArray);
        }
        for (int removeWindow = window.size() - 1; removeWindow >= 0; removeWindow--){
            window.remove(removeWindow);
        }
    }
 
    public void deCompressAndSaveAs(String fileName){
        FileWriter fw = null;
        String firstWindowWrite = "";
        for (int writeInit = 0; writeInit < WINDOW_SIZE; writeInit++){
            firstWindowWrite += fileArray.get(writeInit);
        }
        try {
            fw = new FileWriter(fileName, true);
            fw.write(firstWindowWrite, 0, firstWindowWrite.length());
            fw.close();
        } catch (IOException i){
            System.out.println("Error: " + i.getMessage());
        }
        
        // Decompression Techniques
        for (int deCompress = 0; deCompress < fileArray.size() - WINDOW_SIZE; deCompress++){
        	for (int fillWindow = deCompress; fillWindow < WINDOW_SIZE + deCompress; fillWindow++){
        		window.add(fileArray.get(fillWindow));
        	}
        	// System.out.println("\n" + window + "\n");
        	if (fileArray.get((deCompress + WINDOW_SIZE)) == (char)7){
        		int whereToStart = (int)(fileArray.get(deCompress + WINDOW_SIZE + 1));
        		int whereToEnd = (int)(fileArray.get(deCompress + WINDOW_SIZE + 2)) + whereToStart;
        		// System.out.println("\nStart:" + whereToStart + ", End:" + whereToEnd + "\n");
        		for (int removeSpecialChars = 0; removeSpecialChars < 3; removeSpecialChars++){
        			fileArray.remove((deCompress + WINDOW_SIZE));
        		}
        		// System.out.println(fileArray);
        		int moveUpChar = 0;
        		for (int startPoint = whereToStart; startPoint < whereToEnd; startPoint++){
        			fileArray.add((deCompress + WINDOW_SIZE + moveUpChar), window.get(startPoint));
        			String charToWrite = window.get(startPoint) + "";
        			// System.out.print(charToWrite);
        			try {
        				fw = new FileWriter(fileName, true);
        				fw.write(charToWrite, 0, charToWrite.length());
        				fw.close();
        			} catch (IOException io){
        				System.out.println("Error: " + io.getMessage());
        			}
        			moveUpChar++;
        		}
        		// System.out.println(fileArray);
        		deCompress += ((whereToEnd - whereToStart - 1));
        	} else if (fileArray.get((deCompress + WINDOW_SIZE)) != (char)7) {
        		String charToWrite = fileArray.get((deCompress + WINDOW_SIZE)) + "";
        		try {
        			fw = new FileWriter(fileName, true);
        			fw.write(charToWrite, 0, charToWrite.length());
        			fw.close();
        		} catch (IOException io){
        			System.out.println("Error: " + io.getMessage());
        		}
        	}
        	for (int clearWindow = 29; clearWindow >= 0; clearWindow--){
        		window.remove(clearWindow);
        	}
        }  
        
        // Get the length of the output file
        File outputFile = new File(fileName);
        decompressedOutFileSize = outputFile.length();
    }

    public void printStats() {
        System.out.printf("Original size = %,d bytes\n", inFileSize);
        System.out.printf("Compressed size = %,d bytes\n", outFileSize);
        System.out.printf("Compression ratio = %.1f%s\n\n", 100 * (1.0 - outFileSize/(double)inFileSize), "%");
    }
    
    public void printDecompStats(){
    	System.out.printf("Original size = %,d bytes\n", inFileSizeSave);
    	System.out.printf("Decompressed size = %,d bytes\n", decompressedOutFileSize);
    	if (inFileSizeSave == decompressedOutFileSize) {
        	System.out.println("Input File Size = Decompressed Output File Size, No bytes lost in decompression!");
        }
    }
   
    public static void main(String[] args) {
        SlidingWindow demo = new SlidingWindow();
        demo.readFile("guten.txt");
        demo.compressAndSaveAs("guten_output.txt");
        demo.printStats();
        if (runDeCompress == true) {
            demo.readFile("guten_output.txt");
            demo.deCompressAndSaveAs("guten_decompress.txt");
            demo.printDecompStats();
        }
    }
}
