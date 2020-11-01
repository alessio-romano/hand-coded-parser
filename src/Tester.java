public class Tester {

    public static void main(String[] args) throws Exception {

        if (args.length == 0) {
            throw new Exception("Path del file non presente come argomento");
        }

        String filePath = args[0];
        RecDesParser parser = new RecDesParser(filePath);

        boolean isValid = parser.S();
        if (isValid) {
            System.out.println("The input string is valid.");
        } else {
            System.out.println("The input string is invalid.");
        }

        parser.printStringTable();
    }
}