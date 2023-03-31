package com.ssafy.dodo.controller;

import com.ssafy.dodo.dto.*;
import com.ssafy.dodo.entity.ExpDiary;
import com.ssafy.dodo.service.BucketListService;
import com.ssafy.dodo.service.ExpDiaryService;
import com.ssafy.dodo.service.PublicBucketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/bucketlists")
public class BucketListController {

    private final BucketListService bucketListService;
    private final PublicBucketService publicBucketService;
    private final ExpDiaryService expDiaryService;

    @GetMapping("/{bucketlist-seq}")
    @ResponseStatus(HttpStatus.OK)
    public DataResponse<Map<String, Object>> getBucketListInfo(
            @PathVariable("bucketlist-seq") Long bucketListSeq,
            @AuthenticationPrincipal UserDetails userDetails) {
        return new DataResponse<>(bucketListService.getBucketListInfo(userDetails, bucketListSeq));
    }

    @GetMapping("/{bucketlist-seq}/buckets")
    @ResponseStatus(HttpStatus.OK)
    public DataResponse<?> getBucketListBuckets(
            @PathVariable("bucketlist-seq") Long bucketListSeq,
            @AuthenticationPrincipal UserDetails userDetails){

        return new DataResponse<>(bucketListService.getBucketListBuckets(userDetails, bucketListSeq));
    }

    @PostMapping("/{bucketlist-seq}/buckets")
    @ResponseStatus(HttpStatus.CREATED)
    public DataResponse<?> addCustomBucket(
            @PathVariable("bucketlist-seq") Long bucketListSeq,
            @RequestBody CustomBucketDto customBucketDto,
            @AuthenticationPrincipal UserDetails userDetails
    ){
        return new DataResponse<>(publicBucketService.addCustomBucket(bucketListSeq, customBucketDto, userDetails));
    }

    @PostMapping("/{bucketlist-seq}/buckets/{public-bucket-seq}/search")
    @ResponseStatus(HttpStatus.CREATED)
    public DataResponse<?> addSearchedBucket(
            @PathVariable("bucketlist-seq") Long bucketListSeq,
            @PathVariable("public-bucket-seq") Long publicBucketSeq,
            @AuthenticationPrincipal UserDetails userDetails
    ){
        return new DataResponse<>(bucketListService.addSearchedBucket(bucketListSeq, publicBucketSeq, userDetails));
    }

    @PostMapping("/{bucketlist-seq}/buckets/{public-bucket-seq}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addBucket(
            @PathVariable("bucketlist-seq") Long bucketListSeq,
            @PathVariable("public-bucket-seq") Long publicBucketSeq,
            @AuthenticationPrincipal UserDetails userDetails
    ){
        bucketListService.addSearchedBucket(bucketListSeq, publicBucketSeq, userDetails);
    }

    @PatchMapping("/{bucketlist-seq}")
    @ResponseStatus(HttpStatus.OK)
    public DataResponse<?> updateBucketListInfo(
            @PathVariable("bucketlist-seq") Long bucketListSeq,
            @RequestPart("bucketlistinfo") BucketListInfoDto bucketListInfoDto,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails
    ){
         return new DataResponse<>(bucketListService.updateBucketListInfo(bucketListSeq, bucketListInfoDto, file, userDetails));
    }

    @DeleteMapping("/{bucketlist-seq}")
    @ResponseStatus(HttpStatus.OK)
    public DataResponse<?> deleteBucketList(
            @PathVariable("bucketlist-seq") Long bucketListSeq,
            @AuthenticationPrincipal UserDetails userDetails
    ){
       return new DataResponse<>(bucketListService.deleteBucketList(bucketListSeq, userDetails));
    }

    @GetMapping("/{bucketlist-seq}/diaries")
    public DataResponse<Page<ExpDiaryInfoDto>> getExpDiariesByBucketList(
            @PathVariable("bucketlist-seq") Long bucketListSeq,
            Pageable pageable,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userSeq = Long.parseLong(userDetails.getUsername());
        List<ExpDiary> expDiaries = expDiaryService.getExpDiaryByBucketList(userSeq, bucketListSeq, pageable);
        List<ExpDiaryInfoDto> content = expDiaries.stream()
                .map(ExpDiaryInfoDto::of)
                .collect(Collectors.toList());

        Page<ExpDiaryInfoDto> data = new PageImpl<>(content, pageable, content.size());
        return new DataResponse<>(data);
    }

    @PostMapping("/{bucketlist-seq}/invite")
    public DataResponse<Map<String, String>> createInviteToken(
            @PathVariable("bucketlist-seq") Long bucketListSeq,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long inviterSeq = Long.parseLong(userDetails.getUsername());
        String inviteToken = bucketListService.createInviteToken(bucketListSeq, inviterSeq);

        Map<String, String> data = new HashMap<>();
        data.put("inviteToken", inviteToken);

        return new DataResponse<>(data);
    }

    @PostMapping("/join/{invite-token}")
    public CommonResponse joinBucketList(
            @PathVariable("invite-token") String inviteToken,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long participantSeq = Long.parseLong(userDetails.getUsername());
        bucketListService.joinBucketList(participantSeq, inviteToken);
        return new CommonResponse(true);
    }
}