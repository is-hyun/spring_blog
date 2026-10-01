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

    // GET - http://localhost:8080/    ,    http://localhost:8080/board/list
    @GetMapping({"/", "/board/list"})
    public String list(Model model) {

//        // 뼈대용 임시 데이터
//        model.addAttribute("boardList", List.of(
//                Map.of("id", 1, "title", "첫 번째 글"),
//                Map.of("id", 2, "title", "두 번째 글"),
//                Map.of("id", 3, "title", "세 번째 글")
//        ));

        List<Board> boardList = boardNativeRepository.findAll();
        model.addAttribute("boardList", boardList);

        return "board/list";
    }

    // GET - http://localhost:8080/board/2
    @GetMapping("/board/{id}")
    public String detail(@PathVariable(name = "id") Long id, Model model) {
        Board board = boardNativeRepository.findById(id);
        if (board == null) {
            return "redirect:/";
        }

        model.addAttribute("board", board);
        return "board/detail";
    }

    // GET - http://localhost:8080/board/save
    @GetMapping("/board/save")
    public String saveForm() {

        return "board/save-form";
    }

    // [코드 추가]
    // GET - http://localhost:8080/board/save
    // 스프링 부트의 데이터 기본 파싱 전략 key=value
    // name 속성 기준으로 값을 추출할 수 있다
    @PostMapping("/board/save")
    public String save(@RequestParam("username") String username,
                       @RequestParam("title") String title,
                       @RequestParam("content") String content) {
        // 폼의 name 속성과 매개변수명이 일치하면 자동으로 값이 바인딩 됨
        // name="title" --> String title로 자동 매핑

        log.info("username : {}", username);
        log.info("title : {}", title);
        log.info("content : {}", content);

        // DAO 객체에 데이터를 전달 후 저장하는 일 위임
        boardNativeRepository.save(title, content, username);

        // redirect: - 저장 후 메인 페이지로 이동
        // POST 요청 후 redirect로 = RPG(Post-Redirect-Get) 패턴
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
        boardNativeRepository.deleteById(id);
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
