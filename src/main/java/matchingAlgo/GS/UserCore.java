package  matchingAlgo.GS;


public class UserCore{
    public String id;

    public boolean reserved;
    public String reservedBy;
    public boolean delete;

    public UserCore(String id){
        this.id = id;
        this.reserved = false;
        this.reservedBy = "";
        this.delete = false;
    }
}