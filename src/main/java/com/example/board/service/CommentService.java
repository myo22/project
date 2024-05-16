package com.example.board.service;

import com.example.board.Repository.*;
import com.example.board.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tensorflow.Graph;
import org.tensorflow.Session;
import org.tensorflow.Tensor;
import org.tensorflow.Tensors;

import javax.persistence.criteria.Path;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final AssignmentRepository assignmentRepository;
    private final VideoRepository videoRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    private Session session;

//    @Transactional
//    public Comment addCommentToAssignment(int assignmentId, String content, User user){
//        Assignment assignment = assignmentRepository.findById(assignmentId).orElseThrow();
//        Comment comment = Comment.builder()
//                .content(content)
//                .user(user)
//                .assignment(assignment)
//                .build();
//        return commentRepository.save(comment);
//    }
//
//    @Transactional
//    public Comment addCommentToVideo(int videoId, String content, User user){
//        Video video = videoRepository.findById(videoId).orElseThrow();
//        Comment comment = Comment.builder()
//                .content(content)
//                .user(user)
//                .video(video)
//                .build();
//        return commentRepository.save(comment);
//    }

    @Transactional
    public Comment addComment(int contentId, String content, User user, String contentType) {
        switch (contentType) {
            case "assignment":
                Assignment assignment = assignmentRepository.findById(contentId).orElseThrow(() -> new IllegalArgumentException("Assignment not found"));
                return commentRepository.save(new Comment(content, user, assignment, null));
            case "video":
                Video video = videoRepository.findById(contentId).orElseThrow(() -> new IllegalArgumentException("Video not found"));
                return commentRepository.save(new Comment(content, user, null, video));
            default:
                throw new IllegalArgumentException("Unsupported content type: " + contentType);
        }
    }

    @Transactional
    public List<Comment> getCommentsByAssignment(Assignment assignment){

        return commentRepository.findByAssignment(assignment);
    }

    @Transactional
    public List<Comment> getCommentByVideo(Video video){
        return commentRepository.findByVideo(video);
    }

    public List<Comment> getCommentByCourse(Course course){
        return commentRepository.findByCourseId(course.getCourseId());
    }

    @Transactional
    public List<Comment> getCommentByUser(User user){
        return commentRepository.findByUser(user);
    }

//    public List<Comment> analyzeComments(Assignment assignment) {
//        List<Comment> comments = commentRepository.findByAssignment(assignment);
//        return comments;
//    }
//
//    public List<String> analyzeComments(Video video) {
//        List<Comment> comments = commentRepository.findByVideo(video);
//        return extractImportantComments(comments);
//    }


//    private List<String> extractImportantComments(List<Comment> comments) {
//        Map<String, Set<String>> morphToCommentsMap = new HashMap<>();
//        Komoran komoran = new Komoran(DEFAULT_MODEL.LIGHT);
//        for (Comment comment : comments) {
//            String cleanedComment = comment.getContent().trim().replaceAll("\\s+", " ").toLowerCase();
//            List<Token> tokens = komoran.analyze(cleanedComment).getTokenList();
//            for (Token token : tokens) {
//                String morph = token.getMorph();
//                morphToCommentsMap.computeIfAbsent(morph, k -> new HashSet<>()).add(comment.getContent());
//            }
//        }
//        return getTopQuestions(morphToCommentsMap, 3);
//    }


//    public List<String> getTopQuestions(Map<String, Set<String>> morphToCommentsMap , int topN){
//        return morphToCommentsMap.entrySet().stream()
//                .sorted((e1, e2) -> Integer.compare(e2.getValue().size(), e1.getValue().size()))
//                .limit(topN)
//                .flatMap(e -> e.getValue().stream())
//                .distinct()
//                .collect(Collectors.toList());
//    }


    // TF-IDF를 사용하여 댓글을 벡터화하는 함수
    public Map<String, Map<String, Double>> calculateTFIDF(List<String> comments) {
        Map<String, Map<String, Double>> tfidfMatrix = new HashMap<>();
        int totalComments = comments.size();

        // 각 댓글의 단어 빈도수 계산
        Map<String, Integer> wordCountMap = new HashMap<>();
        for (String comment : comments) {
            String[] words = comment.split("\\s+");
            for (String word : words) {
                wordCountMap.put(word, wordCountMap.getOrDefault(word, 0) + 1);
            }
        }

        // TF 계산
        for (String comment : comments) {
            String[] words = comment.split("\\s+");
            Map<String, Double> tfMap = new HashMap<>();
            for (String word : words) {
                double tf = (double) Collections.frequency(Arrays.asList(words), word) / words.length;
                tfMap.put(word, tf);
            }
            tfidfMatrix.put(comment, tfMap);
        }

        // IDF 계산
        Map<String, Double> idfMap = new HashMap<>();
        for (String word : wordCountMap.keySet()) {
            int wordCount = wordCountMap.get(word);
            double idf = Math.log((double) totalComments / (wordCount + 1));
            idfMap.put(word, idf);
        }

        // TF-IDF 계산
        for (String comment : comments) {
            Map<String, Double> tfidfMap = tfidfMatrix.get(comment);
            for (String word : tfidfMap.keySet()) {
                double tfidf = tfidfMap.get(word) * idfMap.getOrDefault(word, 0.0);
                tfidfMap.put(word, tfidf);
            }
        }

        return tfidfMatrix;
    }

    // 코사인 유사도 계산 함수
    public double calculateCosineSimilarity(Map<String, Double> vector1, Map<String, Double> vector2) {
        double dotProduct = 0.0;
        double magnitude1 = 0.0;
        double magnitude2 = 0.0;

        for (String word : vector1.keySet()) {
            dotProduct += vector1.get(word) * vector2.getOrDefault(word, 0.0);
            magnitude1 += Math.pow(vector1.get(word), 2);
        }

        for (String word : vector2.keySet()) {
            magnitude2 += Math.pow(vector2.get(word), 2);
        }

        magnitude1 = Math.sqrt(magnitude1);
        magnitude2 = Math.sqrt(magnitude2);

        if (magnitude1 == 0.0 || magnitude2 == 0.0) {
            return 0.0;
        } else {
            return dotProduct / (magnitude1 * magnitude2);
        }
    }


    // 댓글 간의 유사도를 계산하는 함수
    public Map<String, Map<String, Double>> calculateCommentSimilarities(Map<String, Map<String, Double>> tfidfMatrix) {
        Map<String, Map<String, Double>> commentSimilarities = new HashMap<>();

        for (String comment1 : tfidfMatrix.keySet()) {
            Map<String, Double> similarityMap = new HashMap<>();
            Map<String, Double> vector1 = tfidfMatrix.get(comment1); // comment1에 대한 벡터 가져오기

            for (String comment2 : tfidfMatrix.keySet()) {
                if (!comment1.equals(comment2)) {
                    Map<String, Double> vector2 = tfidfMatrix.get(comment2); // comment2에 대한 벡터 가져오기
                    double similarity = calculateCosineSimilarity(vector1, vector2); // 유사도 계산
                    similarityMap.put(comment2, similarity);
                }
            }
            commentSimilarities.put(comment1, similarityMap);
        }

        return commentSimilarities;
    }
//    public Map<String, Map<String, Double>> calculateCommentSimilarities(Map<String, Map<String, Double>> tfidfMatrix) {
//        Map<String, Map<String, Double>> commentSimilarities = new HashMap<>();
//
//        for (String comment1 : tfidfMatrix.keySet()) {
//            Map<String, Double> similarityMap = new HashMap<>();
//            for (String comment2 : tfidfMatrix.keySet()) {
//                if (!comment1.equals(comment2)) {
//                    double similarity = calculateCosineSimilarity(tfidfMatrix.get(comment1), tfidfMatrix.get(comment2));
//                    similarityMap.put(comment2, similarity);
//                }
//            }
//            commentSimilarities.put(comment1, similarityMap);
//        }
//
//        return commentSimilarities;
//    }

    // 주요 댓글 추출 함수
    public List<String> extractImportantComments(Map<String, Map<String, Double>> commentSimilarities, int topN) {
        List<String> importantComments = new ArrayList<>();

        for (String comment : commentSimilarities.keySet()) {
            Map<String, Double> similarityMap = commentSimilarities.get(comment);
            double score = 0.0;
            for (String otherComment : similarityMap.keySet()) {
                score += similarityMap.get(otherComment);
            }
            importantComments.add(comment + " : " + score); // 여기서는 간단하게 점수를 더한 값을 사용합니다.
        }

        // 점수에 따라 정렬하여 상위 N개의 주요 댓글을 추출합니다.
        importantComments.sort((c1, c2) -> Double.compare(Double.parseDouble(c2.split(":")[1].trim()), Double.parseDouble(c1.split(":")[1].trim())));
        importantComments = importantComments.subList(0, Math.min(topN, importantComments.size()));

        return importantComments;
    }

    public List<String> recommendImportantCommentsForUser(User user, int topN) {
        List<Course> courses =  courseRepository.findByUser(user);
        List<String> recommendedComments = new ArrayList<>();

        for (Course course : courses) {
//            Set<User> participants = course.getParticipants();
//            for(User participant : participants){
//                List<Comment> comments = commentService.getCommentByUser(participant);
//                for (Comment comment : comments) {
//                    commentTexts.add(comment.getContent());
//                }
//            }
            List<Comment> comments = commentRepository.findByCourseId(course.getCourseId());
            List<String> commentTexts = new ArrayList<>();
            for (Comment comment : comments) {
                commentTexts.add(comment.getContent());
            }
            // TF-IDF를 사용하여 댓글 벡터화
            Map<String, Map<String, Double>> tfidfMatrix = calculateTFIDF(commentTexts);
            // 댓글 간 유사도 계산
            Map<String, Map<String, Double>> commentSimilarities = calculateCommentSimilarities(tfidfMatrix);
            // 개선된 주요 댓글 추출
            List<String> importantCommentsForCourse = extractImportantComments(commentSimilarities, topN);
            recommendedComments.addAll(importantCommentsForCourse);
        }

        return recommendedComments;
    }

    // BERT 모델 로드
    public BERTCommentRecommender() {
        // TensorFlow 그래프 로드
        try (Graph graph = new Graph()) {
            byte[] graphDef = readAllBytesOrExit(Paths.get("path/to/bert_model.pb"));
            graph.importGraphDef(graphDef);
            // TensorFlow 세션 시작
            session = new Session(graph);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // BERT 모델을 사용하여 댓글을 임베딩하는 함수
    public List<double[]> embedComments(List<String> comments) {
        List<double[]> embeddings = new ArrayList<>();

        for (String comment : comments) {
            // BERT 모델에 입력으로 들어갈 텐서 생성
            Tensor<String> inputTensor = Tensors.create(new String[]{comment});
            // BERT 모델 실행
            List<Tensor<?>> outputs = session.runner()
                    .feed("input_comment", inputTensor) // 모델의 입력 노드 이름에 맞게 수정
                    .fetch("embedding_output") // 모델의 출력 노드 이름에 맞게 수정
                    .run();

            // 임베딩된 벡터 추출
            float[][] embeddingValues = new float[outputs.size()][];
            for (int i = 0; i < outputs.size(); i++) {
                try (Tensor<?> outputTensor = outputs.get(i)) {
                    FloatBuffer floatBuffer = FloatBuffer.allocate((int) outputTensor.shape()[1]);
                    outputTensor.writeTo(floatBuffer);
                    embeddingValues[i] = floatBuffer.array();
                }
            }

            // float 배열을 double 배열로 변환하여 리스트에 추가
            double[] embedding = new double[embeddingValues[0].length];
            for (int i = 0; i < embeddingValues[0].length; i++) {
                embedding[i] = embeddingValues[0][i];
            }
            embeddings.add(embedding);
        }

        return embeddings;
    }

    // 두 벡터 간의 코사인 유사도 계산
    public double calculateSimilarity(double[] embedding1, double[] embedding2) {
        // 코사인 유사도 계산 로직 작성
        return 0.0; // 여기에 실제 계산 로직을 구현해야 함
    }

    // 중요한 댓글 추출 함수
    public List<String> extractImportantComments(List<String> comments, int topN) {
        // 댓글을 임베딩
        List<double[]> embeddings = embedComments(comments);

        // 임베딩된 댓글 간 유사성 계산
        List<List<Double>> similarities = new ArrayList<>();
        for (int i = 0; i < embeddings.size(); i++) {
            List<Double> similarityRow = new ArrayList<>();
            for (int j = 0; j < embeddings.size(); j++) {
                double similarity = calculateSimilarity(embeddings.get(i), embeddings.get(j));
                similarityRow.add(similarity);
            }
            similarities.add(similarityRow);
        }

        // 중요한 댓글 추출 로직 작성 (예: 상위 N개의 유사성이 높은 댓글 추출)
        List<String> importantComments = new ArrayList<>();
        // 추출 로직 작성
        return importantComments;
    }

    // 파일에서 그래프 정의를 읽는 함수
    private static byte[] readAllBytesOrExit(Path path) throws IOException {
        return Files.readAllBytes(path);
    }

}

