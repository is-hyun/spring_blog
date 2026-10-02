package com.tenco.spring_blog.board;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor // [코드 추가]
@Controller
public class BoardController {

    // [코드 추가]
    private final BoardNativeRepository boardNativeRepository;
    private final BoardPersistRepository boardPersistRepository;

    // GET - http://localhost:8080/    ,    http://localhost:8080/board/list
    @GetMapping({"/", "/board/list"})
    public String list(Model model) {

        List<Board> boardList = boardPersistRepository.findAll();
        model.addAttribute("boardList", boardList);

        return "board/list";
    }

    // GET - http://localhost:8080/board/2
    @GetMapping("/board/{id}")
    public String detail(@PathVariable(name = "id") Long id, Model model) {
        Board boardEntity = boardPersistRepository.findById(id);
        // Board boardEntity = boardPersistRepository.findByIdWithJPQL(id);
        if (boardEntity == null) {
            // 추후에 404 에러 페이지 구현 시 처리
            throw new RuntimeException("게시글을 찾을 수 없습니다 : " + id);
        }

        model.addAttribute("board", boardEntity);
        return "board/detail";
    }

    // GET - http://localhost:8080/board/save
    @GetMapping("/board/save")
    public String saveForm() {

        return "board/save-form";
    }

    @PostMapping("/board/save")
    // Spring이 폼 데이터를 객체로 변환하는 과정 (데이터 바인딩 메커니즘)
    // 폼 데이터 바인딩 - Spring이 HTTP 요청 파라미터를 객체로 자동 변환
    public String save(BoardRequest.SaveDto reqDto) {

        // 1. DTO에서 Entity 클래스로 변환
        // Board board = new Board(reqDto.getTitle(), reqDto.getContent(), reqDto.getUsername());
        Board board = Board.builder()
                .title(reqDto.getTitle())
                .content(reqDto.getContent())
                .username(reqDto.getUsername())
                .build();
        Board boardEntity = boardPersistRepository.save(board);
        
        return "redirect:/";
    }

    // GET - http://localhost:8080/board/1/update
    @GetMapping("/board/{id}/update")
    public String updateForm(@PathVariable Long id, Model model) {
        Board board = boardNativeRepository.findById(id);
        model.addAttribute("board", board);

        return "board/update-form";
    }

    // POST - http://localhost:8080/board/1/update
    @PostMapping("/board/{id}/update")
    public String update(@PathVariable Long id,
                         @RequestParam(name = "title") String title,
                         @RequestParam(name = "content") String content) {

        boardNativeRepository.updateById(title, content, id);
        // PRG
        return "redirect:/board/" + id;
    }

    // 게시글 삭제
    @PostMapping("/board/{id}/delete")
    public String delete(@PathVariable Long id) {
        boardPersistRepository.deleteById(id);
        // PRG 패턴
        return "redirect:/";
    }

    // TODO
    // 뼈대용 임시 게시글 (DB 연결 시 삭제)
    private Map<String, Object> sampleBoard(Long id) {
        return Map.of("id", id,
                "title", id + "번째 글",
                "content", "임시 내용...",
                "username", "김민수"
        );
    }
}
