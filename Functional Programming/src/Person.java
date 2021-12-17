public class Person implements Comparable<Person> {
    private String name;
    private int age;


    public Person(String name, int age){
        this.name = name;
        this.age = age;
    }

    public int getAge(){
        return this.age;
    }

    public String getName(){
        return this.name;
    }

    @Override
    public int compareTo(Person o) {
        return this.age - o.getAge();
    }

    public String toString(){
        return name + " " + age;
    }
}
