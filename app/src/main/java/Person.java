public class Person {
    private String name;
    private int ID;
    private int phoneNumber;
    private double age;
    private double weight;
    private double hight;
    private int gender; // 1 = female, 0 = male
    private int tag; // 1 = trainee, 0 = trainer
    private String mail;

    public Person(){
        this.name = "";
        this.ID = 0;
        this.phoneNumber = 0;
        this.age = 0.0;
        this.weight = 0.0;
        this.hight = 0.0;
        this.gender = 0; // 1 = female, 0 = male
        this.tag = 1; // 1 = trainee, 0 = trainer
        this.mail = "";
    }

    public Person(String name, int id, int number, double age, double weight, double hight, int gender, int tag, String mail){
        this.name = name;
        this.ID = id;
        this.phoneNumber = number;
        this.age = age;
        this.weight = weight;
        this.hight = hight;
        this.gender = gender; // 1 = female, 0 = male
        this.tag = tag; // 1 = trainee, 0 = trainer
        this.mail = mail;
    }

    // manager constructor
    public Person(String name, int id, int number) {
        this.name = name;
        this.ID = id;
        this.phoneNumber = number;
    }

    public String getName(){
        return this.name;
    }
    public void setName(String new_name){
        this.name = new_name;
    }

    public int getID(){
        return this.ID;
    }
    public void setID(int id){
        this.ID = id;
    }

    public int getPhoneNumber(){
        return this.phoneNumber;
    }
    public void setPhoneNumber(int phoneNumber){
        this.phoneNumber = phoneNumber;
    }

    public double getAge(){
        return this.age;
    }
    public void setAge(double age){
        this.age = age;
    }

    public double getWeight(){
        return this.weight;
    }
    public void setWeight(double weight){
        this.weight = weight;
    }

    public double getHight(){
        return this.hight;
    }
    public void setHight(double hight){
        this.hight = hight;
    }

    public int getGender(){
        return this.gender;
    }
    public void setGender(int gender){
        this.gender = gender;
    }

    public int getTag(){
        return this.tag;
    }
    public void setTag(int tag){
        this.tag = tag;
    }

    public String getMail(){
        return this.mail;
    }
    public void setMail(String mail){
        this.mail = mail;
    }
}
