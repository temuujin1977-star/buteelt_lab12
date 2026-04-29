package mn.csm311.lab12.task2;


public class GreetingService {
    private final UserRepository repository;

    public GreetingService(UserRepository repository) {
        this.repository = repository;
    }

  public String greet(String email) {
    // Optional ашиглан нэг мөрөнд:
    return repository.findByEmail(email)
            .map(user -> "Сайн байна уу, " + user.name() + "!")
            .orElse("Сайн байна уу, Зочин!");
}
}