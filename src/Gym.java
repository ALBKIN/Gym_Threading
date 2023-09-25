import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream
import static java.lang.Thread.currentThread;

public class Gym {
    private final int totalGymMembers;
    private Map<MachineType, Integer> availableMachines;

    public Gym(int totalGymMembers, Map<MachineType, Integer> availableMachines) {
        this.totalGymMembers = totalGymMembers;
        this.availableMachines = availableMachines;
    }

    public void openForTheDay() {
        List<Thread> gymMemberRoutines;
        gymMemberRoutines = IntStream().rangeClosed(1, this.totalGymMembers).mapToObj(
                (id) -> {
                    GymMember gymMember = new GymMember(id);
                    return new Thread(() -> {
                        try {
                           gymMember.performRoutine();
                        } catch (Exception e) {
                            System.out.println("Error occurred for member with ID " + id + ": " + e);
                        }
                    });
                }).collect(Collectors.toList());
        Thread supervisor = createSupervisor(gymMemberRoutines);
        supervisor.start();
        gymMemberRoutines.forEach(Thread::start);
    }

    private Thread createSupervisor(List<Thread> threads) {
        Thread supervisor = new Thread(() -> {
            while(true) {
                // Lambda technique
                // List<String> runningThreads = threads.stream().filter((t) -> t.isAlive()).map((t) -> t.getName()).collect(Collectors.toList())
                List<String> runningThreads = threads.stream().filter(Thread::isAlive).map(Thread::getName).collect(Collectors.toList());
                System.out.println(Thread.currentThread().getName() + " - current number of members working out: " + runningThreads.size());
                System.out.println("Members currently exercising: " + runningThreads + "\n");
                if (runningThreads.isEmpty()) {
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println(e);
                }
            }
            System.out.println(Thread.currentThread().getName() + "\nAll Gym Members have finished their training!");
        });
        supervisor.setName("Gym Staff");
        return supervisor;
    }
}