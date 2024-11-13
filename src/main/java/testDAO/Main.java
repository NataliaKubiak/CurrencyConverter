package testDAO;

import testDAO.models.Person;

public class Main {
    public static void main(String[] args) {
        PersonDAO personDAO = new PersonDAO();

//        System.out.println(personDAO.index());
        Person person = new Person("NEW Derek", 333, "NEWNEEWWWWWWDerek@gmail.com");
//        personDAO.save(person);
        System.out.println(personDAO.show(7));
    }
}
