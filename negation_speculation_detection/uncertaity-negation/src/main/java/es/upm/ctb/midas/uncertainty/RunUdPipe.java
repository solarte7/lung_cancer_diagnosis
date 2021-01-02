package es.upm.ctb.midas.uncertainty;

import cz.cuni.mff.ufal.udpipe.*;
import java.util.Scanner;

class RunUdPipe {
  public static void main(String[] args) {
    if (args.length < 3) {
      System.err.println("Usage: RunUDPipe input_format(tokenize|conllu|horizontal|vertical) output_format(conllu) model_file");
      System.exit(1);
    }

    System.err.print("Loading model: ");
    Model model = Model.load(args[2]);
    if (model == null) {
      System.err.println("Cannot load model from file '" + args[2] + "'");
      System.exit(1);
    }
    System.err.println("done");


    Pipeline pipeline = new Pipeline(model, args[0], Pipeline.getDEFAULT(), Pipeline.getDEFAULT(), args[1]);
    ProcessingError error = new ProcessingError();
    Scanner reader = new Scanner(System.in);

    // Read while input
    StringBuilder textBuilder = new StringBuilder();
    while (reader.hasNextLine()) {
      textBuilder.append(reader.nextLine());
      textBuilder.append('\n');
    }

    // Tokenize and tag
    String text = textBuilder.toString();
    String processed = pipeline.process(text, error);

    if (error.occurred()) {
      System.err.println("Cannot read input CoNLL-U: " + error.getMessage());
      System.exit(1);
    }
    System.out.print(processed);
  }
}