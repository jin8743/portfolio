package com.portfolio.response;

import com.portfolio.domain.Comment;
import com.portfolio.domain.MemberRole;
import com.portfolio.domain.Post;

import java.util.ArrayList;
import java.util.List;

public class MemberResponse {

    private Long id;

    private String username;

    private MemberRole role;

    private List<Comment> comments = new ArrayList<>();

    private List<Post> posts = new ArrayList<>();

}
