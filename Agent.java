package sokoban;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Agent {
	public static void main(String[] args) throws IOException {
		final String solutionPath = new File(System.getProperty("user.dir")).toPath().resolve("src/main/java/sokoban/solution.txt").toString();
		File f = new File(solutionPath);
		FileReader fileReader = new FileReader(f);
		BufferedReader reader = new BufferedReader(fileReader);
		String solution = reader.readLine();
        for (char c : solution.toCharArray()) System.out.println(c);
        reader.close();
	}
}