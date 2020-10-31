public class Token {

    private int name;// questo è un identificativo di token: potrebbe anche essere un intero
    private String attribute;

    public Token(int name, String attribute){
        this.name = name;
        this.attribute = attribute;
    }

    public Token(int name){
        this.name = name;
        this.attribute = null;
    }

    public int getName() {
        return name;
    }

    public void setName(int name) {
        this.name = name;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public boolean sameTokenType(int type){
        if(type == this.name){
            return true;
        }

        return false;
    }

    public boolean equals(Token obj) {
        if(obj.attribute != null){
            if((obj.name == this.name) && obj.attribute.equals(this.attribute)){
                return true;
            }
        } else {
            if(obj.name == this.name){
                return true;
            }
        }

        return false;
    }

    public String toString(){
        return attribute==null? "<"+name+">" : "<"+name+", \""+attribute+"\">";
    }
}
