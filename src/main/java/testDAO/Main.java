package testDAO;

public class Main {
    public static void main(String[] args) {
        Config config = new Config();
        PersonDAO personDAO = new PersonDAO(config.jdbcTemplate());

//        System.out.println(personDAO.index());
//        Person person = new Person("JdbcPerson", 1, "JdbcPerson@gmail.com");
//        personDAO.save(person);
        personDAO.delete(4);
//        System.out.println(personDAO.show(7));
    }
}
