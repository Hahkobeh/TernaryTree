import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TernaryTree {


    public static void main(String[] args) {
        Node root = new Node();
        String[] s = new String[2000];
        int i = 0;
        boolean failure = false;






        Pattern addNode = Pattern.compile("^Add([LMR])\\((.+)\\)$");
        Pattern removeNode = Pattern.compile("^Del([LMR])\\((.+)\\)$");
        Pattern exchangeNode = Pattern.compile("^Exchange\\((.+)\\)$");
        Pattern printTree = Pattern.compile("^Print\\(\\)$");


        try {
            BufferedReader br = new BufferedReader(new FileReader(args[0]));


            while ((s[i] = br.readLine()) != null) {
                i++;
            }
            br.close();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            int j = 0;
            String toWrite = "";
            BufferedWriter bw = new BufferedWriter(new FileWriter(args[1], false));
            while (s[j] != null) {
                boolean writting = false;
                String [] arguments = {"",""};
                Matcher addMatcher = addNode.matcher(s[j]);
                Matcher removeMatcher = removeNode.matcher(s[j]);
                Matcher exchangeMatcher = exchangeNode.matcher(s[j]);
                Matcher printMatcher = printTree.matcher(s[j]);





                if(addMatcher.find()){
                    String original = addMatcher.group(2);
                    if(original.contains(" ") || dealWithCommas(arguments, original) || original.charAt(0) == ',' || arguments[1].equals("$") || arguments[1].equals("")){
                        failure = true;
                    }else if(arguments[1].charAt(0) != '$') {
                        Node useNode = nodeSearch('r', root, arguments[0]);
                        if (useNode != null) {
                            if (useNode.hasChild(addMatcher.group(1).charAt(0))) {
                                toWrite += "Add operation not possible.";
                                //bw.write("Add operation not possible.");
                                //bw.flush();
                                writting = true;
                            } else {
                                Node createNode = new Node(useNode, arguments[1], addMatcher.group(1).charAt(0));
                            }
                        }
                    }else {
                        arguments[1] = arguments[1].substring(1);
                        Node useNode = nodeSearch('r', root, arguments[0]);
                        if (useNode != null) {
                            if (useNode.hasChild(addMatcher.group(1).charAt(0))) {
                                useNode.getChild(addMatcher.group(1).charAt(0)).setPayload(arguments[1]);
                            } else {
                                Node createNode = new Node(useNode, arguments[1], addMatcher.group(1).charAt(0));
                            }
                        }
                    }

                }else if(removeMatcher.find()){
                    String original = removeMatcher.group(2);
                    if(original.contains(" ")){
                        failure = true;
                    }
                    Node useNode = nodeSearch('l',root,original);
                    if(useNode != null){
                        useNode.removeChild(removeMatcher.group(1).charAt(0));
                    }


                }else if(exchangeMatcher.find()){
                    String original = exchangeMatcher.group(1);
                    if(original.contains(" ") || dealWithCommas(arguments, original)){
                        failure = true;
                    }else if(arguments[1].charAt(0) != '$') {
                        nodeExchange(root, arguments[1], arguments[0]);
                    }else {
                        arguments[1] = arguments[1].substring(1);
                        nodeExchangeModified(root, arguments[1], arguments[0]);


                    }

                }else if(printMatcher.find()) {
                    Node [] nodeArray = nodePrint(root);
                    int g = 0, c = 0, t;
                    boolean test = false;
                    int highestLevel = 0;
                    while (nodeArray[g] != null) {
                        if(nodeArray[g].getLevel() > highestLevel){
                            highestLevel = nodeArray[g].getLevel();
                        }
                        g++;
                    }
                    for(int h = 0; h <= highestLevel; h++){

                        while(nodeArray[c] != null){
                            if(nodeArray[c].getLevel() == h){
                                toWrite += nodeArray[c].getPayload();
                                //bw.write(nodeArray[c].getPayload());
                                t = c + 1;
                                while(nodeArray[t] != null){
                                    if (nodeArray[t].getLevel() == nodeArray[c].getLevel()) {
                                        test = true;
                                        break;
                                    }
                                    t++;
                                }
                                if(test) {
                                    toWrite += " ; ";
                                    //bw.write(" ; ");
                                }
                                if(!test){
                                    if(!(h == highestLevel)) {
                                        toWrite += "\n";
                                        //bw.newLine();
                                    }
                                }
                                test = false;
                            }
                            c++;
                        }
                        c = 0;
                    }
                    writting = true;


                    // go through array find highest level, then create 2d array and fill each row with the nodes from that level, print 2d array where one row is one line
                }else {
                    bw.write(toWrite);
                    bw.write("Input error.");
                    bw.flush();
                    bw.close();
                    System.exit(1);
                }

                if(failure){
                    bw.write(toWrite);
                    bw.write("Input error.");
                    bw.flush();
                    bw.close();
                    System.exit(1);
                }



                if(s[j+1] != null){
                    if(writting) {
                        toWrite += "\n";
                        //bw.newLine();
                        //bw.flush();
                    }
                }

                j++;
            }
            if(toWrite.endsWith("\n")){
                toWrite = toWrite.substring(0,toWrite.length()-1);
            }

            bw.write(toWrite);
            bw.flush();
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean dealWithCommas(String[] arguments, String original){
        int j = 0;
        for(int i = 0; i < original.length(); i++){
            if(original.charAt(i) == ',' && j == 0){
                if(i == original.length() - 1){
                    return true;
                }

            }

            if(original.charAt(i) == ','  && j != 1){
                if( original.charAt(i + 1) != ','){
                    j = 1;
                    continue;
                }
            }
            arguments[j] += original.charAt(i);
        }
        return false;
    }

    public static Node nodeSearch(char leftright, Node rootNode, String findPayload){
        if(findPayload.equals("root")){
            return rootNode;
        }

        Node foundNode = null;
        Node [] allNodes = new Node[5000];
        int k = 0, p = 0, j = 0;
        recursiveFill(allNodes,rootNode,p);

        while(allNodes[k] != null){
            if(allNodes[k].getPayload().equals(findPayload)){
                if(leftright == 'r'){
                    if(allNodes[k].getLevel() >= j){
                        j = allNodes[k].getLevel();
                        foundNode = allNodes[k];
                    }
                }

                if(leftright == 'l'){
                    if(allNodes[k].getLevel() > j){
                        j = allNodes[k].getLevel();
                        foundNode = allNodes[k];
                    }
                }

            }

            k++;
        }



        return foundNode;
    }

    public static void nodeExchange(Node rootNode, String replacement, String toBeReplaced){
        Node [] allNodes = new Node[5000];
        int k = 0, p = 0;
        recursiveFill(allNodes,rootNode,p);

        while(allNodes[k] != null){
            if(allNodes[k].getPayload().equals(toBeReplaced)){
                allNodes[k].setPayload(replacement);
            }

            k++;
        }
    }

    public static void nodeExchangeModified(Node rootNode, String replacement, String toBeReplaced){
        Node [] allNodes = new Node[5000];
        int k = 0, p = 0;
        recursiveFill(allNodes,rootNode,p);

        while(allNodes[k] != null){
            if(allNodes[k].getPayload().equals(toBeReplaced)){
                allNodes[k].setPayload(toBeReplaced + replacement);
            }

            k++;
        }
    }


    public static Node [] nodePrint(Node rootNode){
        Node [] allNodes = new Node[5000];
        int p = 0;
        recursiveFill(allNodes,rootNode,p);
        return allNodes;
    }

    public static int recursiveFill(Node [] allNodes, Node currentNode, int p){
        allNodes[p] = currentNode;
        if(!currentNode.hasChildren()){
            return p;
        }
        if(currentNode.hasChildLeft()) {
            p = recursiveFill(allNodes, currentNode.getLeft(), p + 1);

        }
        if(currentNode.hasChildMiddle()) {
            p = recursiveFill(allNodes, currentNode.getMiddle(), p + 1);
        }
        if(currentNode.hasChildRight()) {
            p = recursiveFill(allNodes, currentNode.getRight(), p + 1);
        }
        return p;
    }


}

class Node{
    private String payload;
    private final int level;
    private final Node parent;
    private Node left;
    private Node middle;
    private Node right;

    public Node() {
        this.payload = "root";
        this.parent = null;
        this.level = 0;
    }

    public Node(Node parentNode, String payload, char side){
        this.parent = parentNode;
        this.payload = payload;
        this.level = parent.getLevel() + 1;
        if(side == 'L'){
            parent.addNodeLeft(this);
        }
        if(side == 'M'){
            parent.addNodeMiddle(this);
        }
        if(side == 'R'){
            parent.addNodeRight(this);
        }

    }

    public Node getParent() {
        return parent;
    }

    public boolean hasChildren(){
        return left != null || middle != null || right != null;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String replacement){
        this.payload = replacement;
    }



    public int getLevel() {
        return level;
    }



    public void addNodeLeft(Node node){
        this.left = node;
    }

    public void addNodeMiddle(Node node){
        this.middle = node;
    }

    public void addNodeRight(Node node){
        this.right = node;
    }

    public void removeNodeLeft(){
        this.left = null;
    }

    public void removeNodeMiddle(){
        this.middle = null;
    }

    public void removeNodeRight(){
        this.right = null;
    }

    public void removeChild(char side){
        if(side == 'R'){
            removeNodeRight();
        }
        if(side == 'M'){
            removeNodeMiddle();
        }
        if(side == 'L'){
            removeNodeLeft();
        }
    }

    public boolean hasChildLeft(){
        return this.left != null;
    }

    public boolean hasChildMiddle(){
        return this.middle != null;
    }

    public boolean hasChildRight(){
        return this.right != null;
    }

    public boolean hasChild(char side){
        if(side == 'R'){
            return hasChildRight();
        }
        if(side == 'M'){
            return hasChildMiddle();
        }
        if(side == 'L'){
            return hasChildLeft();
        }
        return false;
    }

    public Node getChild(char side){
        if(side == 'R'){
            return getRight();
        }
        if(side == 'M'){
            return getMiddle();
        }
        if(side == 'L'){
            return getLeft();
        }
        return null;
    }

    public Node getLeft() {
        return left;
    }

    public Node getMiddle() {
        return middle;
    }

    public Node getRight() {
        return right;
    }
}


