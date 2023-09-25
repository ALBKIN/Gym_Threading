import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.Thread.currentThread;

public class Gym {
    private final int totalGymMembers;
    private Map<MachineType, Integer> availableMachines;

    public Gym(int totalGymMembers, Map<MachineType, Integer> availableMachines) {
        this.totalGymMembers = totalGymMembers;
        this.availableMachines = availableMachines;
    }

    public static void main(String[] args) {
        Gym goldsGym = new Gym(5, new HashMap<>() {
            {
                put(MachineType.LEGPRESSMACHINE, 5);
                put(MachineType.BARBELL, 5);
                put(MachineType.SQUATMACHINE, 5);
                put(MachineType.LEGEXTENSIONMACHINE, 5);
                put(MachineType.LEGCURLMACHINE, 5);
                put(MachineType.LATPULLDOWNMACHINE, 5);
                put(MachineType.CABLECROSSOVERMACHINE, 5);
            }
        });
        goldsGym.openForTheDay();
    }

    public void openForTheDay() {
        List<Thread> gymMemberRoutines;
        gymMemberRoutines = IntStream.rangeClosed(1, this.totalGymMembers).mapToObj(
                (id) -> {
                    GymMember gymMember = new GymMember(id);
                    Thread memberThread = new Thread(() -> {
                        try {
                            gymMember.performRoutine();
                        } catch (Exception e) {
                            System.out.println("Error occurred for member with ID " + id + ": " + e);
                        }
                    });
                    memberThread.setName("JuiceHead " + id);
                    return memberThread;
                }).collect(Collectors.toList());
        Thread supervisor = createSupervisor(gymMemberRoutines);
        supervisor.start();
        gymMemberRoutines.forEach(Thread::start);
    }

    private Thread createSupervisor(List<Thread> threads) {
        Thread supervisor = new Thread(() -> {
            while (true) {
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