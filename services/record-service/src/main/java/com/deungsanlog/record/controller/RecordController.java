package com.deungsanlog.record.controller;

import com.deungsanlog.record.dto.HotMountainResponse;
import com.deungsanlog.record.dto.RankingsResponse;
import com.deungsanlog.record.dto.RecordHikingResponse;
import com.deungsanlog.record.service.HotMountainService;
import com.deungsanlog.record.service.RankingService;
import com.deungsanlog.record.service.RecordHikingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class RecordController {

    private final RecordHikingService recordHikingService;
    private final HotMountainService hotMountainService;
    private final RankingService rankingService;

    @GetMapping("/status")
    public Map<String, String> getStatus() {
        return Map.of("message", "record-service is up!");
    }

    @GetMapping("/get")
    public ResponseEntity<Page<RecordHikingResponse>> getRecordsByUser(
            @RequestParam("userId") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("recordDate").descending());
        return ResponseEntity.ok(recordHikingService.getRecordsByUser(userId, pageable));
    }


    @PostMapping("/post")
    public ResponseEntity<String> createRecord(
            @RequestParam("userId") Long userId,
            @RequestParam(value = "mountainId", required = false) Long mountainId,
            @RequestParam("mountainName") String mountainName,
            @RequestParam("recordDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate recordDate,
            @RequestParam(value = "content", required = false) String content,
            @RequestParam("photo") MultipartFile photo
    ) {
        recordHikingService.create(userId, mountainId, mountainName, recordDate, content, photo);
        return ResponseEntity.ok("등산 기록이 성공적으로 저장되었습니다!");
    }

    @GetMapping("/record/{recordId}")
    public ResponseEntity<RecordHikingResponse> getRecordById(
            @PathVariable("recordId") Long recordId
    ) {
        RecordHikingResponse record = recordHikingService.getRecordById(recordId);
        return ResponseEntity.ok(record);
    }

    @GetMapping("/hot-mountains")
    public ResponseEntity<List<HotMountainResponse>> getHotMountains(
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(hotMountainService.getHotMountains(limit));
    }

    @GetMapping("/rankings")
    public ResponseEntity<RankingsResponse> getRankings(@RequestParam(required = false) Long userId) {
        RankingsResponse response = rankingService.getRankingInfo(userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/edit")
    public ResponseEntity<String> editRecord(
            @RequestParam("recordId") Long recordId,
            @RequestParam(value = "mountainName", required = false) String mountainName,
            @RequestParam(value = "recordDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate recordDate,
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "photo", required = false) MultipartFile photo
    ) {
        recordHikingService.edit(recordId, mountainName, recordDate, content, photo);
        return ResponseEntity.ok("등산 기록이 성공적으로 수정되었습니다!");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteRecord(@RequestParam("recordId") Long recordId) {
        recordHikingService.delete(recordId);
        return ResponseEntity.ok("등산 기록이 성공적으로 삭제되었습니다!");
    }


}