package com.example.board.service;

import org.apache.commons.lang3.tuple.Pair;
import com.example.board.Repository.*;
import com.example.board.domain.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.math3.linear.RealMatrix;
import org.springframework.web.client.RestTemplate;


import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final AssignmentRepository assignmentRepository;
    private final VideoRepository videoRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final NoticeRepository noticeRepository;

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
                return commentRepository.save(new Comment(content, user, assignment, null, null));
            case "video":
                Video video = videoRepository.findById(contentId).orElseThrow(() -> new IllegalArgumentException("Video not found"));
                return commentRepository.save(new Comment(content, user, null, video, null));
            case "notice":
                Notice notice = noticeRepository.findById(contentId).orElseThrow(() -> new IllegalArgumentException("Notice not found"));
                return commentRepository.save(new Comment(content, user, null, null, notice));
            default:
                throw new IllegalArgumentException("Unsupported content type: " + contentType);
        }
    }

    @Transactional
    public Set<Comment> getCommentsByAssignment(int assignmentId) {

        return commentRepository.findByAssignmentAssignmentIdOrderByCreatedAtDesc(assignmentId);
    }

    @Transactional
    public Set<Comment> getCommentByVideo(int videoId) {
        return commentRepository.findByVideoVideoId(videoId);
    }

    @Transactional
    public Set<Comment> getCommentByNotice(int noticeId) {return commentRepository.findByNoticeNoticeId(noticeId);}

    public List<Comment> getCommentByCourse(Course course) {
        return commentRepository.findByCourseId(course.getCourseId());
    }

    @Transactional
    public List<Comment> getCommentByUser(User user) {
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

    // 불용어 리스트
    private static final Set<String> STOP_WORDS = Set.of("그", "저", "것", "수", "등", "을", "를", "가", "에", "의", "으로", "들");

    // 텍스트 전처리 함수
    public String preprocessText(String text) {
        // 소문자 변환, 구두점 제거 등
        String cleanedText = text.replaceAll("[^\\p{IsAlphabetic}\\s]", "").toLowerCase();
        // 불용어 제거
        return Arrays.stream(cleanedText.split("\\s+"))
                .filter(word -> !STOP_WORDS.contains(word))
                .collect(Collectors.joining(" "));
    }

    private Map<String, Map<String, Double>> tfidfMatrix;

    private float[] getTFIDFVector(String comment) {
        String[] words = preprocessText(comment).split("\\s+");
        float[] vector = new float[tfidfMatrix.size()]; // tfidfMatrix는 전역 변수로 유지
        int index = 0;

        for (String word : tfidfMatrix.keySet()) {
            vector[index] = tfidfMatrix.getOrDefault(comment, new HashMap<>()).getOrDefault(word, 0.0).floatValue();
            index++;
        }
        return vector;
    }

    public double callSiameseApi(float[] comment1Vector, float[] comment2Vector) {
        // Python API 호출 및 유사도 계산
        String url = "http://localhost:5000/predict";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String requestBody = String.format("{\"comment1\": [%s], \"comment2\": [%s]}",
                Arrays.toString(comment1Vector), Arrays.toString(comment2Vector));

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            // 유사도 값 추출
            String responseBody = response.getBody();
            return Double.parseDouble(responseBody.split(":")[1].replace("}", "").trim());
        } else {
            throw new RuntimeException("Error calling Siamese API");
        }
    }

    // 댓글 쌍을 생성하는 함수
    public List<Pair<String, String>> createCommentPairs(List<String> comments) {
        List<Pair<String, String>> pairs = new ArrayList<>();
        for (int i = 0; i < comments.size(); i++) {
            for (int j = i + 1; j < comments.size(); j++) {
                pairs.add(Pair.of(comments.get(i), comments.get(j)));
            }
        }
        return pairs;
    }

    // Siamese Network을 통한 유사도 계산
    public List<Pair<String, Double>> getSiameseSimilarity(List<Pair<String, String>> commentPairs) {
        List<Pair<String, Double>> similarities = new ArrayList<>();
        for (Pair<String, String> pair : commentPairs) {
            // TF-IDF 벡터화
            String comment1 = pair.getLeft();
            String comment2 = pair.getRight();
            float[] comment1Vector = getTFIDFVector(comment1);
            float[] comment2Vector = getTFIDFVector(comment2);

            // Python API를 통해 Siamese Network 모델에 유사도 계산 요청
            double similarity = callSiameseApi(comment1Vector, comment2Vector);
            similarities.add(Pair.of(comment1, similarity));
        }
        return similarities;
    }

    public List<String> extractImportantComments(List<Pair<String, Double>> similarities, int topN) {
        return similarities.stream()
                .sorted((a, b) -> Double.compare(b.getRight(), a.getRight())) // 유사도 기준 내림차순 정렬
                .limit(topN)
                .map(Pair::getLeft) // 댓글 텍스트 추출
                .collect(Collectors.toList());
    }

    public List<String> recommendCosineComments(User user, int topN) {
        List<Course> courses = courseRepository.findByUser(user);
        List<String> recommendedComments = new ArrayList<>();

        List<String> allCommentTexts = new ArrayList<>();
        for (Course course : courses) {
            List<Comment> comments = commentRepository.findByCourseId(course.getCourseId());
            for (Comment comment : comments) {
                allCommentTexts.add(comment.getContent());
            }
        }

        // 댓글 쌍 생성
        List<Pair<String, String>> commentPairs = createCommentPairs(allCommentTexts);

        // Siamese Network을 통한 유사도 계산
        List<Pair<String, Double>> similarities = getSiameseSimilarity(commentPairs);

        // 유사도에 따라 상위 N개의 댓글 추천
        recommendedComments = extractImportantComments(similarities, topN);

        return recommendedComments;
    }

//    private Map<String, Integer> wordToIndex; // 단어를 인덱스에 매핑하는 맵
//    private Map<String, Integer> wordCountMap; // 단어의 빈도를 저장하는 맵
//    private RealMatrix tfidfMatrix; // TF-IDF 행렬
//    private RealMatrix similarityMatrix; // 유사도 행렬
//
//
//    // TF-IDF를 사용하여 댓글을 벡터화하는 함수
//    public Map<String, Map<String, Double>> calculateTFIDF(List<String> comments) {
//        Map<String, Map<String, Double>> tfidfMatrix = new HashMap<>();
//        int totalComments = comments.size();
//
//        // 각 댓글의 단어 빈도수 계산
//        Map<String, Integer> wordCountMap = new HashMap<>();
//        for (String comment : comments) {
//            String[] words = preprocessText(comment).split("\\s+");
//            for (String word : words) {
//                wordCountMap.put(word, wordCountMap.getOrDefault(word, 0) + 1);
//            }
//        }
//
//        // TF 계산
//        for (String comment : comments) {
//            String[] words = preprocessText(comment).split("\\s+");
//            Map<String, Double> tfMap = new HashMap<>();
//            for (String word : words) {
//                double tf = (double) Collections.frequency(Arrays.asList(words), word) / words.length;
//                tfMap.put(word, tf);
//            }
//            tfidfMatrix.put(comment, tfMap);
//        }
//
//        // IDF 계산
//        Map<String, Double> idfMap = new HashMap<>();
//        for (String word : wordCountMap.keySet()) {
//            int wordCount = wordCountMap.get(word);
//            double idf = Math.log((totalComments + 1.0) / (wordCount + 1.0)) + 1.0; // 스무딩 적용
//            idfMap.put(word, idf);
//        }
//
//        // TF-IDF 계산
//        for (String comment : comments) {
//            Map<String, Double> tfidfMap = tfidfMatrix.get(comment);
//            for (String word : tfidfMap.keySet()) {
//                double tfidf = tfidfMap.get(word) * idfMap.getOrDefault(word, 0.0);
//                tfidfMap.put(word, tfidf);
//            }
//        }
//
//        return tfidfMatrix;
//    }
//
//    // 코사인 유사도 계산 함수
//    public double calculateCosineSimilarity(Map<String, Double> vector1, Map<String, Double> vector2) {
//        double dotProduct = 0.0;
//        double magnitude1 = 0.0;
//        double magnitude2 = 0.0;
//
//        for (String word : vector1.keySet()) {
//            dotProduct += vector1.get(word) * vector2.getOrDefault(word, 0.0);
//            magnitude1 += Math.pow(vector1.get(word), 2);
//        }
//
//        for (String word : vector2.keySet()) {
//            magnitude2 += Math.pow(vector2.get(word), 2);
//        }
//
//        magnitude1 = Math.sqrt(magnitude1);
//        magnitude2 = Math.sqrt(magnitude2);
//
//        if (magnitude1 == 0.0 || magnitude2 == 0.0) {
//            return 0.0;
//        } else {
//            return dotProduct / (magnitude1 * magnitude2);
//        }
//    }
//
//
//    // 댓글 간의 유사도를 계산하는 함수
//    public Map<String, Map<String, Double>> calculateCommentSimilarities(Map<String, Map<String, Double>> tfidfMatrix) {
//        Map<String, Map<String, Double>> commentSimilarities = new HashMap<>();
//
//        for (String comment1 : tfidfMatrix.keySet()) {
//            Map<String, Double> similarityMap = new HashMap<>();
//            Map<String, Double> vector1 = tfidfMatrix.get(comment1); // comment1에 대한 벡터 가져오기
//
//            for (String comment2 : tfidfMatrix.keySet()) {
//                if (!comment1.equals(comment2)) {
//                    Map<String, Double> vector2 = tfidfMatrix.get(comment2); // comment2에 대한 벡터 가져오기
//                    double similarity = calculateCosineSimilarity(vector1, vector2); // 유사도 계산
//                    similarityMap.put(comment2, similarity);
//                }
//            }
//            commentSimilarities.put(comment1, similarityMap);
//        }
//
//        return commentSimilarities;
//    }
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
//
//    // 주요 댓글 추출 함수
//    public List<String> extractImportantComments(Map<String, Map<String, Double>> commentSimilarities, int topN) {
//        List<String> importantComments = new ArrayList<>();
//        Map<String, Double> commentScores = new HashMap<>();
//
//        for (String comment : commentSimilarities.keySet()) {
//            Map<String, Double> similarityMap = commentSimilarities.get(comment);
//            double score = 0.0;
//            for (Double similarity : similarityMap.values()) {
//                score += similarity;
//            }
//            commentScores.put(comment, score); // 댓글과 점수를 맵에 저장합니다.
//        }
//
//        // 점수에 따라 정렬하여 상위 N개의 주요 댓글을 추출합니다.
//        importantComments = commentScores.entrySet().stream()
//                .sorted((entry1, entry2) -> Double.compare(entry2.getValue(), entry1.getValue()))
//                .limit(topN)
//                .map(Map.Entry::getKey)
//                .collect(Collectors.toList());
//
//        return importantComments;
//    }
//
//    // 기존에 사용하는 추천 시스템
//    public List<String> recommendCosineComments(User user, int topN) {
//        List<Course> courses = courseRepository.findByUser(user);
//        List<String> recommendedComments = new ArrayList<>();
//
//        List<String> allCommentTexts = new ArrayList<>();
//
//        for (Course course : courses) {
//            List<Comment> comments = commentRepository.findByCourseId(course.getCourseId());
//            List<String> commentTexts = new ArrayList<>();
//            for (Comment comment : comments) {
//                commentTexts.add(comment.getContent());
//            }
//            allCommentTexts.addAll(commentTexts);
//        }
//        // TF-IDF를 사용하여 댓글 벡터화
//        Map<String, Map<String, Double>> tfidfMatrix = calculateTFIDF(allCommentTexts);
//        // 댓글 간 유사도 계산
//        Map<String, Map<String, Double>> commentSimilarities = calculateCommentSimilarities(tfidfMatrix);
//        // 개선된 주요 댓글 추출
//        recommendedComments = extractImportantComments(commentSimilarities, topN);
//
//        return recommendedComments;
//    }
//
//    // 모델 학습
//    public void trainModel(List<String> comments) {
//        wordToIndex = new HashMap<>();
//        wordCountMap = new HashMap<>();
//        List<String> uniqueWords = new ArrayList<>();
//        int index = 0;
//
//        // 각 댓글의 단어 빈도수 계산
//        for (String comment : comments) {
//            comment = preprocessText(comment); // 전처리 적용
//            String[] words = comment.split("\\s+");
//            for (String word : words) {
//                wordCountMap.put(word, wordCountMap.getOrDefault(word, 0) + 1);
//                if (!wordToIndex.containsKey(word)) {
//                    wordToIndex.put(word, index++);
//                    uniqueWords.add(word);
//                }
//            }
//        }
//
//        int totalComments = comments.size();
//        int totalWords = uniqueWords.size();
//
//        // TF-IDF 행렬 초기화
//        tfidfMatrix = new Array2DRowRealMatrix(totalComments, totalWords);
//
//        // TF-IDF 값 계산
//        for (int i = 0; i < totalComments; i++) {
//            String comment = comments.get(i);
//            comment = preprocessText(comment); // 전처리 적용
//            String[] words = comment.split("\\s+");
//            for (String word : words) {
//                Integer wordIndex = wordToIndex.get(word);
//                if (wordIndex == null) {
//                    System.err.println("단어 인덱스를 찾을 수 없습니다: " + word);
//                    continue; // 또는 예외를 던지거나 적절히 처리합니다.
//                }
//                double tf = (double) wordCountMap.get(word) / words.length;
//                double idf = Math.log((double) (totalComments + 1) / (wordCountMap.get(word) + 1)) + 1;
//                tfidfMatrix.setEntry(i, wordIndex, tf * idf);
//            }
//        }
//
//
//        // 유사도 행렬 계산 (코사인 유사도)
//        similarityMatrix = tfidfMatrix.multiply(tfidfMatrix.transpose());
//    }
//
//    // 주어진 새로운 댓글의 중요도 예측
//    public double predictCommentImportance(String comment) {
//        comment = preprocessText(comment); // 전처리 적용
//        String[] words = comment.split("\\s+");
//        double[] commentVector = new double[tfidfMatrix.getColumnDimension()];
//
//        for (String word : words) {
//            if (wordToIndex.containsKey(word)) {
//                int wordIndex = wordToIndex.get(word);
//                double tf = (double) Collections.frequency(Arrays.asList(words), word) / words.length;
//                double idf = Math.log((double) tfidfMatrix.getRowDimension() / (wordCountMap.getOrDefault(word, 0) + 1));
//                commentVector[wordIndex] = tf * idf;
//            }
//        }
//
//        RealMatrix commentMatrix = new Array2DRowRealMatrix(new double[][]{commentVector});
//        RealMatrix similarityVector = commentMatrix.multiply(tfidfMatrix.transpose());
//
//        double importance = similarityVector.getNorm() / (commentMatrix.getNorm() * tfidfMatrix.transpose().getNorm());
//
//        return importance;
//    }
//
//    // 주요 댓글 추천
//    public List<String> recommendModelComments(User user, int topN) {
//        List<String> recommendedComments = new ArrayList<>();
//        Map<String, Double> commentImportanceMap = new HashMap<>();
//        List<Course> courses = courseRepository.findByUser(user);
//
//        for (Course course : courses) {
//            List<Comment> comments = commentRepository.findByCourseId(course.getCourseId());
//            List<String> commentTexts = new ArrayList<>();
//            for (Comment comment : comments) {
//                commentTexts.add(comment.getContent());
//            }
//
//            if (commentTexts.isEmpty()) {
//                continue;
//            }
//
//            trainModel(commentTexts);
//
//            for (String comment : commentTexts) {
//                double importance = predictCommentImportance(comment);
//                commentImportanceMap.put(comment, importance);
//            }
//
//        }
//
//        // 중요도에 따라 정렬하여 상위 N개의 주요 댓글을 추출합니다.
//        commentImportanceMap.entrySet().stream()
//                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
//                .limit(topN)
//                .forEach(entry -> recommendedComments.add(entry.getKey()));
//
//        return recommendedComments;
//    }
//
//    // 주요 댓글 추천
//    public Map<String, List<String>> recommendModelComments(User user, int topN) {
//        Map<String, List<String>> recommendedComments = new HashMap<>();
//        Map<String, Double> commentImportanceMap = new HashMap<>();
//        List<Course> courses = courseRepository.findByUser(user);
//
//        for (Course course : courses) {
//            List<Comment> comments = commentRepository.findByCourseId(course.getCourseId());
//            List<String> commentTexts = new ArrayList<>();
//            for (Comment comment : comments) {
//                commentTexts.add(comment.getContent());
//            }
//
//            if (commentTexts.isEmpty()) {
//                continue;
//            }
//
//            trainModel(commentTexts);
//
//            for (String comment : commentTexts) {
//                double importance = predictCommentImportance(comment);
//                commentImportanceMap.put(comment, importance);
//            }
//        }
//
//        // 중요도에 따라 정렬하여 댓글을 대, 중, 소로 나누고 상위 N개의 주요 댓글을 추출합니다.
//        List<Map.Entry<String, Double>> sortedEntries = commentImportanceMap.entrySet().stream()
//                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
//                .collect(Collectors.toList());
//
//        int totalComments = sortedEntries.size();
//        int chunkSize = totalComments / 3;
//
//        // 대 구간
//        List<Map.Entry<String, Double>> highImportanceComments = sortedEntries.subList(0, chunkSize);
//        // 중 구간
//        List<Map.Entry<String, Double>> mediumImportanceComments = sortedEntries.subList(chunkSize, 2 * chunkSize);
//        // 소 구간
//        List<Map.Entry<String, Double>> lowImportanceComments = sortedEntries.subList(2 * chunkSize, totalComments);
//
//        recommendedComments.put("highImportance", getTopNComments(highImportanceComments, topN));
//        recommendedComments.put("mediumImportance", getTopNComments(mediumImportanceComments, topN));
//        recommendedComments.put("lowImportance", getTopNComments(lowImportanceComments, topN));
//
//        return recommendedComments;
//    }
//
//    private List<String> getTopNComments(List<Map.Entry<String, Double>> comments, int topN) {
//        return comments.stream()
//                .limit(topN)
//                .map(Map.Entry::getKey)
//                .collect(Collectors.toList());
//    }
//
//    // 피어슨 상관계수
//    public double calculatePearsonCorrelation(Map<String, Double> vector1, Map<String, Double> vector2) {
//        double sum1 = 0.0;
//        double sum2 = 0.0;
//        double sum1Sq = 0.0;
//        double sum2Sq = 0.0;
//        double pSum = 0.0;
//        int n = 0;
//
//        Set<String> commonKeys = new HashSet<>(vector1.keySet());
//        commonKeys.retainAll(vector2.keySet());
//
//        if (commonKeys.size() == 0) {
//            return 0.0; // 공통된 단어가 없는 경우 0 반환
//        }
//
//        for (String key : commonKeys) {
//            double val1 = vector1.get(key);
//            double val2 = vector2.get(key);
//
//            sum1 += val1;
//            sum2 += val2;
//            sum1Sq += Math.pow(val1, 2);
//            sum2Sq += Math.pow(val2, 2);
//            pSum += val1 * val2;
//            n++;
//        }
//
//        double num = pSum - (sum1 * sum2 / n);
//        double den = Math.sqrt((sum1Sq - Math.pow(sum1, 2) / n) * (sum2Sq - Math.pow(sum2, 2) / n));
//
//        if (den == 0) {
//            return 0.0;
//        }
//
//        return num / den;
//    }
//
//    // 댓글 간의 유사도를 계산하는 함수
//    public Map<String, Map<String, Double>> calculateCommentSimilaritiesUsingPearson(Map<String, Map<String, Double>> tfidfMatrix) {
//        Map<String, Map<String, Double>> commentSimilarities = new HashMap<>();
//
//        for (String comment1 : tfidfMatrix.keySet()) {
//            Map<String, Double> similarityMap = new HashMap<>();
//            Map<String, Double> vector1 = tfidfMatrix.get(comment1); // comment1에 대한 벡터 가져오기
//
//            for (String comment2 : tfidfMatrix.keySet()) {
//                if (!comment1.equals(comment2)) {
//                    Map<String, Double> vector2 = tfidfMatrix.get(comment2); // comment2에 대한 벡터 가져오기
//                    double similarity = calculatePearsonCorrelation(vector1, vector2); // 피어슨 상관계수 계산
//                    similarityMap.put(comment2, similarity);
//                }
//            }
//            commentSimilarities.put(comment1, similarityMap);
//        }
//
//        return commentSimilarities;
//    }
//
//    // 주요 댓글 추천
//    public List<String> recommendPearsonComments(User user, int topN) {
//        List<Course> courses = courseRepository.findByUser(user);
//        List<String> recommendedComments = new ArrayList<>();
//
//        List<String> allCommentTexts = new ArrayList<>();
//
//        for (Course course : courses) {
//            List<Comment> comments = commentRepository.findByCourseId(course.getCourseId());
//            List<String> commentTexts = new ArrayList<>();
//            for (Comment comment : comments) {
//                commentTexts.add(comment.getContent());
//            }
//            allCommentTexts.addAll(commentTexts);
//        }
//        // TF-IDF를 사용하여 댓글 벡터화
//        Map<String, Map<String, Double>> tfidfMatrix = calculateTFIDF(allCommentTexts);
//        // 댓글 간 유사도 계산 (피어슨 상관계수 사용)
//        Map<String, Map<String, Double>> commentSimilarities = calculateCommentSimilaritiesUsingPearson(tfidfMatrix);
//        // 개선된 주요 댓글 추출
//        recommendedComments = extractImportantComments(commentSimilarities, topN);
//
//        return recommendedComments;
//    }
//
//    // 자카드 유사도
//    public double calculateJaccardSimilarity(Set<String> set1, Set<String> set2) {
//        Set<String> intersection = new HashSet<>(set1);
//        intersection.retainAll(set2);
//
//        Set<String> union = new HashSet<>(set1);
//        union.addAll(set2);
//
//        if (union.size() == 0) {
//            return 0.0; // 두 집합이 공통된 원소가 없는 경우
//        }
//
//        return (double) intersection.size() / union.size();
//    }
//
//
//    // 댓글 간의 유사도를 계산하는 함수
//    public Map<String, Map<String, Double>> calculateCommentSimilaritiesUsingJaccard(List<String> comments) {
//        Map<String, Map<String, Double>> commentSimilarities = new HashMap<>();
//
//        for (String comment1 : comments) {
//            Map<String, Double> similarityMap = new HashMap<>();
//            Set<String> words1 = new HashSet<>(Arrays.asList(comment1.split("\\s+")));
//
//            for (String comment2 : comments) {
//                if (!comment1.equals(comment2)) {
//                    Set<String> words2 = new HashSet<>(Arrays.asList(comment2.split("\\s+")));
//                    double similarity = calculateJaccardSimilarity(words1, words2); // 자카드 유사도 계산
//                    similarityMap.put(comment2, similarity);
//                }
//            }
//            commentSimilarities.put(comment1, similarityMap);
//        }
//
//        return commentSimilarities;
//    }
//
//
//    // 주요 댓글 추천
//    public List<String> recommendJaccardForUser(User user, int topN) {
//        List<Course> courses = courseRepository.findByUser(user);
//        List<String> recommendedComments = new ArrayList<>();
//
//        List<String> allCommentTexts = new ArrayList<>();
//
//        for (Course course : courses) {
//            List<Comment> comments = commentRepository.findByCourseId(course.getCourseId());
//            List<String> commentTexts = new ArrayList<>();
//            for (Comment comment : comments) {
//                commentTexts.add(comment.getContent());
//            }
//            allCommentTexts.addAll(commentTexts);
//        }
//        // 댓글 간 유사도 계산 (자카드 유사도 사용)
//        Map<String, Map<String, Double>> commentSimilarities = calculateCommentSimilaritiesUsingJaccard(allCommentTexts);
//        // 개선된 주요 댓글 추출
//        recommendedComments = extractImportantComments(commentSimilarities, topN);
//
//        return recommendedComments;
//    }

}
