package com.example.tricolv2sb.Controller;

import com.example.tricolv2sb.DTO.CreateGoodsIssueDTO;
import com.example.tricolv2sb.DTO.ReadGoodsIssueDTO;
import com.example.tricolv2sb.DTO.UpdateGoodsIssueDTO;
import com.example.tricolv2sb.Service.ServiceInterfaces.GoodsIssueServiceInterface;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/goods-issues")
public class GoodsIssueController {

    private final GoodsIssueServiceInterface goodsIssueService;

    /**
     * GET /api/v1/goods-issues
     * Gets a list of all goods issues
     */
    @GetMapping
    public ResponseEntity<List<ReadGoodsIssueDTO>> getAllGoodsIssues() {
        List<ReadGoodsIssueDTO> goodsIssues = goodsIssueService.fetchAllGoodsIssues();
        return ResponseEntity.ok(goodsIssues);
    }

    /**
     * GET /api/v1/goods-issues/{id}
     * Gets a single goods issue by its ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReadGoodsIssueDTO> getGoodsIssueById(@PathVariable Long id) {
        return goodsIssueService.fetchGoodsIssueById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * POST /api/v1/goods-issues
     * Creates a new goods issue (status: DRAFT)
     */
    @PostMapping
    public ResponseEntity<ReadGoodsIssueDTO> createGoodsIssue(@Valid @RequestBody CreateGoodsIssueDTO dto) {
        ReadGoodsIssueDTO newGoodsIssue = goodsIssueService.createGoodsIssue(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newGoodsIssue);
    }

    /**
     * PUT /api/v1/goods-issues/{id}
     * Updates an existing goods issue (only if status is DRAFT)
     */
    @PutMapping("/{id}")
    public ResponseEntity<ReadGoodsIssueDTO> updateGoodsIssue(
            @PathVariable Long id,
            @Valid @RequestBody UpdateGoodsIssueDTO dto) {
        ReadGoodsIssueDTO updatedGoodsIssue = goodsIssueService.updateGoodsIssue(id, dto);
        return ResponseEntity.ok(updatedGoodsIssue);
    }

    /**
     * DELETE /api/v1/goods-issues/{id}
     * Deletes a goods issue (only if status is DRAFT)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoodsIssue(@PathVariable Long id) {
        goodsIssueService.deleteGoodsIssue(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * PUT /api/v1/goods-issues/{id}/validate
     * Validates a goods issue and creates stock movements OUT
     */
    @PutMapping("/{id}/validate")
    public ResponseEntity<String> validateGoodsIssue(@PathVariable Long id) {
        goodsIssueService.validateGoodsIssue(id);
        return ResponseEntity.ok("Goods issue " + id + " has been validated");
    }

    /**
     * PUT /api/v1/goods-issues/{id}/cancel
     * Cancels a goods issue
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<String> cancelGoodsIssue(@PathVariable Long id) {
        goodsIssueService.cancelGoodsIssue(id);
        return ResponseEntity.ok("Goods issue " + id + " has been cancelled");
    }
}
