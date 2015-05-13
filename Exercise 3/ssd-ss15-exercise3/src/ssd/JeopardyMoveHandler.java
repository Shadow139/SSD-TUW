package ssd;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

/**
 * TODO: Implement this content handler.
 */
public class JeopardyMoveHandler extends DefaultHandler {
	/**
	 * Use this xPath variable to create xPath expression that can be
	 * evaluated over the XML document.
	 */
	private static XPath xPath = XPathFactory.newInstance().newXPath();
	
	/**
	 * Store and manipulate the Jeopardy XML document here.
	 */
	private Document jeopardyDoc;
	
	/**
	 * This variable stores the text content of XML Elements.
	 */
	private String eleText;

	//***TODO***
	//Insert local variables here
    private boolean saxPlayer = false;
    private boolean saxQuestion= false;
    private boolean saxAnswer = false;

    private Move moveObject;
    
	
    public JeopardyMoveHandler(Document doc) {
    	jeopardyDoc = doc;
        moveObject = new Move();
    }
    


    /**
     * 
     * Return the current stored Jeopardy document
     * 
     * @return XML Document
     */
	public Document getDocument() {
		return jeopardyDoc;
	}
    
    //***TODO***
	//Specify additional methods to parse the move document and modify the jeopardyDoc

    @Override
    public void startDocument() throws SAXException{
        System.out.println("        startDocument");

    }

    @Override
    public void startElement(String uri, String localName,
                             String qName, Attributes attributes) throws SAXException {
        if(qName.equalsIgnoreCase("move")) {
            moveObject.setSession(attributes.getValue("session"));
        }

        if(qName.equalsIgnoreCase("player")){
            saxPlayer = true;
        }

        if(qName.equalsIgnoreCase("question")){
            saxQuestion= true;
        }

        if(qName.equalsIgnoreCase("answer")){
            saxAnswer = true;
        }
    }

    @Override
    /**
     * SAX calls this method to pass in character data
     */
    public void characters(char[] text, int start, int length)
            throws SAXException {

        eleText = new String(text, start, length);

        if(saxPlayer){
            System.out.println("Player: " + eleText);
            moveObject.setPlayer(eleText);
            saxPlayer = false;
        }

        if(saxQuestion){
            System.out.println("Question: " + eleText);
            int q = 0;
            try{
                q = Integer.parseInt(eleText);
            }catch(NumberFormatException e){

            }
            moveObject.setQuestion(q);
            saxQuestion= false;
        }

        if(saxAnswer){
            System.out.println("Answer: " + eleText);
            moveObject.addAnswer(eleText);
            saxAnswer = false;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        // no op
    }

    @Override
    public void endDocument() throws SAXException{
        System.out.println("        endDocument");
        System.out.println(moveObject.toString());
        NodeList games = jeopardyDoc.getElementsByTagName("game");

        System.out.println("");
        Node node = getGameWithSession(moveObject.getSession(),games);
        if(node != null) {
            insertMoveIntoXML(node);
        }else{
            System.out.println("Error: node is null");
        }

    }

    public Node getGameWithSession(String session, NodeList games){

        for (int i = 0; i < games.getLength(); i++) {

            Node node = games.item(i);
            System.out.println("NodeList: " + node.getAttributes().getNamedItem("session").getNodeValue());
            System.out.println("session: " + session);

            if(session.equals(node.getAttributes().getNamedItem("session").getNodeValue())){
                    return node;
            }
        }
        return null;
    }

    public void insertMoveIntoXML(Node game){
        NodeList list = game.getChildNodes();
        insertPlayerRef(game,list);
        insertAnswers(game,list);

    }

    public void insertPlayerRef(Node game,NodeList list){
        ArrayList<Node> playerRefList = new ArrayList<Node>();
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            System.out.println("insertPlayerRef: - nodeName: " + node.getNodeName());
            if("player".equals(node.getNodeName())){
                playerRefList.add(node);
                System.out.println("    player found! - size: " + playerRefList.size());
            }
        }

        if(playerRefList.size() == 0){
            createPlayerRef(game, moveObject.getPlayer());
        }else if(playerRefList.size() == 1){
            if(checkExistingPlayerRef(playerRefList.get(0),moveObject.getPlayer())){
                createPlayerRef(game,moveObject.getPlayer());
            }else{
                System.out.println("Error: Player " + moveObject.getPlayer() + " already referenced");
            }
        }else{
            System.out.println("Error: Too many Players in .xml");
        }
    }

    private boolean checkExistingPlayerRef(Node node,String player) {

        if(node.getAttributes().getNamedItem("ref").getNodeValue().equals(player)){
            System.out.println("Player is inside");
            return false;
        }else{
            System.out.println("Player is not inside");
            return true;
        }
    }

    private void createPlayerRef(Node game, String player ) {
        System.out.println("            trying to insert Player: " + player);

        Element elem = jeopardyDoc.createElement("player");
        elem.setAttribute("ref",player);

        Node first = game.getFirstChild();
        game.insertBefore(elem,first);
    }

    private void insertAnswers(Node game, NodeList list) {
        ArrayList<Node> askedList = new ArrayList<Node>();
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            System.out.println("askedList: - nodeName: " + node.getNodeName());
            if("asked".equals(node.getNodeName())){
                askedList.add(node);
            }
        }
        Node asked = getExistingAsked(askedList, moveObject.getQuestion());

        if(asked != null){
            appendAnswers(asked);
        }else{
            createAsked(game);
        }
    }

    private void createAsked(Node game) {

        Element elem = jeopardyDoc.createElement("asked");

        elem.setAttribute("question",moveObject.getQuestion() + "");
        Node asked = game.appendChild(elem);

        for(String answer: moveObject.getAnswerList()){
            Element e = jeopardyDoc.createElement("givenanswer");
            e.setAttribute("player",moveObject.getPlayer());
            e.setNodeValue(answer);
            asked.appendChild(e);
        }
    }

    private void appendAnswers(Node asked) {

    }

    private Node getExistingAsked(ArrayList<Node> askedList, int question) {
        for (int i = 0; i < askedList.size(); i++) {
            Node node = askedList.get(i);
            if((question + "").equals(node.getAttributes().getNamedItem("question").getNodeValue())){
                return node;
            }
        }
        return null;
    }


}

