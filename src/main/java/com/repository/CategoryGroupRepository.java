package com.repository;


import com.entity.custom.CategoryGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryGroupRepository extends JpaRepository<CategoryGroup, Integer> {
    List<CategoryGroup> findAllByIdAndGroupChatId(int groupId, long groupChatId);
    List<CategoryGroup> findByGroupChatId(long groupChatId);
    CategoryGroup deleteByIdAndGroupChatId(int groupId, long groupChatId);
}
