package com.examportal.config;

import com.examportal.entity.*;
import com.examportal.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;
    private final ExamAttemptRepository examAttemptRepository;
    private final StudentAnswerRepository studentAnswerRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email:admin@examportal.com}")
    private String adminEmail;

    @Value("${app.admin.password:Admin@123}")
    private String adminPassword;

    @Value("${app.admin.firstname:System}")
    private String adminFirstName;

    @Value("${app.admin.lastname:Admin}")
    private String adminLastName;

    public DataInitializer(UserRepository userRepository,
                           ExamRepository examRepository,
                           QuestionRepository questionRepository,
                           ExamAttemptRepository examAttemptRepository,
                           StudentAnswerRepository studentAnswerRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.examRepository = examRepository;
        this.questionRepository = questionRepository;
        this.examAttemptRepository = examAttemptRepository;
        this.studentAnswerRepository = studentAnswerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        User admin = ensureAdminUser();
        List<User> students = ensureDemoStudents();
        seedDemoExamsAndAttempts(admin, students);
    }

    private User ensureAdminUser() {
        return userRepository.findByEmail(adminEmail).orElseGet(() -> {
            User admin = User.builder()
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .firstName(adminFirstName)
                    .lastName(adminLastName)
                    .role("ROLE_ADMIN")
                    .enabled(true)
                    .build();
            User saved = userRepository.save(admin);
            log.info("Default admin account created with email: {}", adminEmail);
            return saved;
        });
    }

    private List<User> ensureDemoStudents() {
        List<User> students = new ArrayList<>();
        students.add(createStudentIfMissing("student@examportal.com", "Aarav", "Sharma", "9876543210", "Chandigarh University"));
        students.add(createStudentIfMissing("priya@examportal.com", "Priya", "Verma", "9876543211", "Delhi Technological University"));
        students.add(createStudentIfMissing("rohan@examportal.com", "Rohan", "Mehta", "9876543212", "NIT Kurukshetra"));
        students.add(createStudentIfMissing("sneha@examportal.com", "Sneha", "Patel", "9876543213", "VIT Vellore"));
        students.add(createStudentIfMissing("vikram@examportal.com", "Vikram", "Nair", "9876543214", "SRM Institute"));
        return students;
    }

    private User createStudentIfMissing(String email, String firstName, String lastName, String phone, String college) {
        return userRepository.findByEmail(email).orElseGet(() -> {
            User student = User.builder()
                    .email(email)
                    .password(passwordEncoder.encode("Student@123"))
                    .firstName(firstName)
                    .lastName(lastName)
                    .phone(phone)
                    .college(college)
                    .role("ROLE_STUDENT")
                    .enabled(true)
                    .build();
            User saved = userRepository.save(student);
            log.info("Demo student created with email: {}", email);
            return saved;
        });
    }

    private void seedDemoExamsAndAttempts(User admin, List<User> students) {
        if (examRepository.count() > 0) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.minusDays(7);
        LocalDateTime end = now.plusDays(30);

        Exam javaExam = createExam(admin, "Java Core & OOP Assessment",
                "Covers Java fundamentals, OOP principles, collections, and exception handling.",
                "Java Programming", 30, 10, 40.0, start, end);
        List<Question> javaQuestions = saveQuestions(javaExam, List.of(
                q(javaExam, "Which keyword is used to prevent method overriding in Java?",
                        "static", "final", "abstract", "volatile", "B", "EASY", "OOP"),
                q(javaExam, "Which collection guarantees no duplicate elements and maintains insertion order?",
                        "HashSet", "TreeSet", "LinkedHashSet", "ArrayList", "C", "MEDIUM", "Collections"),
                q(javaExam, "What is the parent class of all checked and unchecked exceptions in Java?",
                        "Error", "RuntimeException", "Throwable", "Exception", "C", "EASY", "Exceptions"),
                q(javaExam, "Which interface must a class implement to be used in a try-with-resources statement?",
                        "Serializable", "AutoCloseable", "Cloneable", "Iterable", "B", "MEDIUM", "Java Core"),
                q(javaExam, "What does JVM stand for?",
                        "Java Variable Machine", "Java Virtual Machine", "Java Verified Memory", "Joint Virtual Module", "B", "EASY", "JVM")
        ));

        Exam springExam = createExam(admin, "Spring Boot & REST APIs",
                "Evaluates knowledge of Spring Boot auto-configuration, Spring MVC, JPA, and REST design.",
                "Backend Development", 45, 10, 50.0, start, end);
        List<Question> springQuestions = saveQuestions(springExam, List.of(
                q(springExam, "Which annotation marks a class as a RESTful controller returning JSON/XML directly?",
                        "@Controller", "@RestController", "@Service", "@Repository", "B", "EASY", "Spring MVC"),
                q(springExam, "Which Spring Boot annotation combines @Configuration, @EnableAutoConfiguration, and @ComponentScan?",
                        "@SpringBootTest", "@SpringApplication", "@SpringBootApplication", "@EnableSpringBoot", "C", "EASY", "Spring Boot"),
                q(springExam, "Which HTTP status code is most appropriate when a new resource is successfully created?",
                        "200 OK", "201 Created", "204 No Content", "202 Accepted", "B", "MEDIUM", "REST"),
                q(springExam, "In Spring Data JPA, which interface provides CRUD and pagination/sorting methods out of the box?",
                        "CrudRepository", "JpaRepository", "EntityManager", "PersistenceContext", "B", "MEDIUM", "Spring Data"),
                q(springExam, "Where are default Spring Boot configuration properties typically placed?",
                        "config.xml", "application.properties", "spring.json", "pom.xml", "B", "EASY", "Configuration")
        ));

        Exam sqlExam = createExam(admin, "Database Management & SQL",
                "Tests SQL queries, joins, normalization, indexing, and ACID transaction concepts.",
                "Databases", 30, 10, 40.0, start, end);
        List<Question> sqlQuestions = saveQuestions(sqlExam, List.of(
                q(sqlExam, "Which SQL clause is used to filter groups created by GROUP BY?",
                        "WHERE", "ORDER BY", "HAVING", "DISTINCT", "C", "EASY", "SQL"),
                q(sqlExam, "Which normal form eliminates transitive functional dependencies?",
                        "1NF", "2NF", "3NF", "UNF", "C", "MEDIUM", "Normalization"),
                q(sqlExam, "What does the 'I' in ACID properties stand for?",
                        "Integrity", "Isolation", "Indexing", "Inheritance", "B", "EASY", "Transactions"),
                q(sqlExam, "Which JOIN returns all rows from the left table and matching rows from the right table?",
                        "INNER JOIN", "RIGHT JOIN", "LEFT JOIN", "CROSS JOIN", "C", "EASY", "Joins"),
                q(sqlExam, "Which SQL command removes all rows from a table without logging individual row deletions?",
                        "DELETE", "DROP", "TRUNCATE", "REMOVE", "C", "MEDIUM", "DDL")
        ));

        Exam dsaExam = createExam(admin, "Data Structures & Algorithms",
                "Focuses on time complexity, trees, graphs, sorting, and linear data structures.",
                "Computer Science", 40, 10, 50.0, start, end);
        List<Question> dsaQuestions = saveQuestions(dsaExam, List.of(
                q(dsaExam, "What is the average-case time complexity of binary search on a sorted array?",
                        "O(n)", "O(log n)", "O(n log n)", "O(1)", "B", "EASY", "Searching"),
                q(dsaExam, "Which data structure follows the Last-In-First-Out (LIFO) principle?",
                        "Queue", "Stack", "Linked List", "Heap", "B", "EASY", "Linear DS"),
                q(dsaExam, "Which traversal of a Binary Search Tree produces elements in sorted ascending order?",
                        "Preorder", "Postorder", "Inorder", "Level order", "C", "MEDIUM", "Trees"),
                q(dsaExam, "Which algorithm is used to find the shortest path from a single source in a graph with non-negative weights?",
                        "Kruskal's", "Prim's", "Dijkstra's", "DFS", "C", "MEDIUM", "Graphs"),
                q(dsaExam, "What is the worst-case time complexity of QuickSort?",
                        "O(n log n)", "O(n)", "O(n^2)", "O(log n)", "C", "MEDIUM", "Sorting")
        ));

        if (examAttemptRepository.count() > 0) {
            return;
        }

        // Seed completed attempts across students so dashboard charts and results pages are populated.
        // Leaves springExam and dsaExam unattempted for student@examportal.com so the test user can also take exams.
        User aarav = students.get(0);
        User priya = students.get(1);
        User rohan = students.get(2);
        User sneha = students.get(3);
        User vikram = students.get(4);

        recordAttempt(aarav, javaExam, javaQuestions, 4, false, now.minusDays(4));
        recordAttempt(aarav, sqlExam, sqlQuestions, 5, false, now.minusDays(2));

        recordAttempt(priya, javaExam, javaQuestions, 5, false, now.minusDays(5));
        recordAttempt(priya, springExam, springQuestions, 4, false, now.minusDays(3));
        recordAttempt(priya, dsaExam, dsaQuestions, 3, false, now.minusDays(1));

        recordAttempt(rohan, javaExam, javaQuestions, 1, true, now.minusDays(5));
        recordAttempt(rohan, sqlExam, sqlQuestions, 3, false, now.minusDays(2));

        recordAttempt(sneha, springExam, springQuestions, 2, false, now.minusDays(3));
        recordAttempt(sneha, dsaExam, dsaQuestions, 4, false, now.minusDays(1));

        recordAttempt(vikram, sqlExam, sqlQuestions, 1, false, now.minusDays(2));

        log.info("Seeded 4 demo exams, 20 questions, and 10 completed attempts.");
    }

    private Exam createExam(User creator, String title, String description, String subject,
                            int durationMinutes, int totalMarks, double passingPercentage,
                            LocalDateTime startTime, LocalDateTime endTime) {
        Exam exam = Exam.builder()
                .title(title)
                .description(description)
                .subject(subject)
                .durationMinutes(durationMinutes)
                .totalMarks(totalMarks)
                .passingPercentage(passingPercentage)
                .startTime(startTime)
                .endTime(endTime)
                .published(true)
                .maxWarnings(3)
                .createdBy(creator)
                .build();
        return examRepository.save(exam);
    }

    private Question q(Exam exam, String text, String a, String b, String c, String d,
                       String correct, String difficulty, String topic) {
        return Question.builder()
                .exam(exam)
                .questionText(text)
                .optionA(a)
                .optionB(b)
                .optionC(c)
                .optionD(d)
                .correctOption(correct)
                .marks(2)
                .difficulty(difficulty)
                .topic(topic)
                .build();
    }

    private List<Question> saveQuestions(Exam exam, List<Question> questions) {
        return questionRepository.saveAll(questions);
    }

    private void recordAttempt(User student, Exam exam, List<Question> questions,
                               int correctCount, boolean autoSubmitted, LocalDateTime endTime) {
        int totalQuestions = questions.size();
        int totalMarks = questions.stream().mapToInt(Question::getMarks).sum();
        int score = 0;

        for (int i = 0; i < totalQuestions; i++) {
            if (i < correctCount) {
                score += questions.get(i).getMarks();
            }
        }

        double percentage = totalMarks > 0 ? (score * 100.0 / totalMarks) : 0.0;
        boolean passed = percentage >= exam.getPassingPercentage();

        String questionOrder = questions.stream()
                .map(question -> String.valueOf(question.getId()))
                .collect(Collectors.joining(","));
        String optionMappings = questions.stream()
                .map(question -> question.getId() + ":ABCD")
                .collect(Collectors.joining(";"));

        ExamAttempt attempt = ExamAttempt.builder()
                .user(student)
                .exam(exam)
                .startTime(endTime.minusMinutes(18))
                .endTime(endTime)
                .totalQuestions(totalQuestions)
                .attempted(totalQuestions)
                .correctAnswers(correctCount)
                .wrongAnswers(totalQuestions - correctCount)
                .score(score)
                .totalMarks(totalMarks)
                .percentage(percentage)
                .passed(passed)
                .status(autoSubmitted ? "AUTO_SUBMITTED" : "COMPLETED")
                .warningCount(autoSubmitted ? exam.getMaxWarnings() : 0)
                .questionOrder(questionOrder)
                .optionMappings(optionMappings)
                .autoSubmitted(autoSubmitted)
                .build();

        ExamAttempt savedAttempt = examAttemptRepository.save(attempt);

        List<StudentAnswer> answers = new ArrayList<>();
        for (int i = 0; i < totalQuestions; i++) {
            Question question = questions.get(i);
            String chosen = i < correctCount
                    ? question.getCorrectOption()
                    : ("A".equals(question.getCorrectOption()) ? "B" : "A");

            answers.add(StudentAnswer.builder()
                    .attempt(savedAttempt)
                    .question(question)
                    .selectedOption(chosen)
                    .markedForReview(false)
                    .answeredAt(endTime.minusMinutes(totalQuestions - i))
                    .build());
        }
        studentAnswerRepository.saveAll(answers);
    }
}
