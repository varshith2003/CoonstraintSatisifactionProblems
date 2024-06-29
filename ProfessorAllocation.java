import java.util.*;

public class ProfessorAllocation {

    static class Professor {
        String name;
        List<String> courses;

        Professor(String name, List<String> courses) {
            this.name = name;
            this.courses = courses;
        }
    }

    public static Map<String, List<String>> allocateProfessors(Map<String, List<String>> professors, List<String> subjects) {
        Map<String, Integer> subjectProfCount = new HashMap<>();
        for (String subject : subjects) {
            subjectProfCount.put(subject, 0);
        }

        // Count professors interested in each subject
        for (List<String> courses : professors.values()) {
            for (String course : courses) {
                subjectProfCount.put(course, subjectProfCount.get(course) + 1);
            }
        }

        // Create a priority queue based on the number of professors interested in subjects
        PriorityQueue<String> priorityQueue = new PriorityQueue<>(Comparator.comparingInt(subjectProfCount::get));
        priorityQueue.addAll(subjects);

        // Initialize professor allocations and workload
        Map<String, List<String>> allocations = new HashMap<>();
        Map<String, Integer> professorWorkload = new HashMap<>();

        // Assign professors to subjects while considering workload constraint
        while (!priorityQueue.isEmpty()) {
            String subject = priorityQueue.poll();
            boolean allocated = false;

            // Find a professor who prefers teaching this subject and has workload < 15 hrs
            for (Map.Entry<String, List<String>> entry : professors.entrySet()) {
                String professor = entry.getKey();
                List<String> courses = entry.getValue();

                if (courses.contains(subject) && professorWorkload.getOrDefault(professor, 0) < 15) {
                    allocations.computeIfAbsent(professor, k -> new ArrayList<>()).add(subject);
                    professorWorkload.put(professor, professorWorkload.getOrDefault(professor, 0) + 3); // Assuming 3 hours per subject per week
                    subjectProfCount.put(subject, subjectProfCount.get(subject) - 1);

                    // Update priority queue if needed
                    if (subjectProfCount.get(subject) < 1) {
                        priorityQueue.remove(subject);
                    }

                    allocated = true;
                    break;
                }
            }

            // Track unallocated subjects
            if (!allocated) {
                allocations.computeIfAbsent("Not allocated", k -> new ArrayList<>()).add(subject);
            }
        }

        return allocations;
    }

    public static void main(String[] args) {
        Map<String, List<String>> professors = new HashMap<>();
        professors.put("Prof A", Arrays.asList("Math", "Physics", "Chemistry"));
        professors.put("Prof B", Arrays.asList("Physics", "Biology"));
        professors.put("Prof C", Arrays.asList("Math", "English", "History"));
        professors.put("Prof D", Arrays.asList("Computer Science", "Math"));
        professors.put("Prof E", Arrays.asList("History", "Geography"));

        List<String> subjects = Arrays.asList("Math", "Physics", "Chemistry", "Biology", "History", "English", "Computer Science", "Geography");

        Map<String, List<String>> allocations = allocateProfessors(professors, subjects);

        // Display allocations including unallocated subjects and workload per week
        for (Map.Entry<String, List<String>> entry : allocations.entrySet()) {
            if (!entry.getKey().equals("Not allocated")) {
                System.out.println(entry.getKey() + " will teach: " + String.join(", ", entry.getValue()));
                int workload = entry.getValue().size() * 3; // 3 hours per subject per week
                System.out.println("Workload per week for " + entry.getKey() + ": " + workload + " hours");
            }
        }

        // Display unallocated subjects
        List<String> unallocated = allocations.getOrDefault("Not allocated", Collections.emptyList());
        if (!unallocated.isEmpty()) {
            System.out.println("Subjects not allocated: " + String.join(", ", unallocated));
        }
    }
}
