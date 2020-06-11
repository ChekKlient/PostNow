package com.postnow.views.dashboard;

import com.postnow.backend.model.Post;
import com.postnow.backend.model.PostComment;
import com.postnow.backend.model.User;
import com.postnow.backend.security.SecurityConfig;
import com.postnow.backend.service.PostCommentService;
import com.postnow.backend.service.PostService;
import com.postnow.backend.service.UserService;
import com.postnow.views.postnow.PostNowView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.IronIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.vaadin.olli.ClipboardHelper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Route(value = "me/dashboard", layout = PostNowView.class)
@PageTitle("Dashboard")
@CssImport(value = "styles/views/dashboard/dashboard-view.css", include = "lumo-badge")
@JsModule("@vaadin/vaadin-lumo-styles/badge.js")
public class DashboardView extends Div implements AfterNavigationObserver{

    @Autowired
    private PostService postService;
    @Autowired
    private UserService userService;
    @Autowired
    private PostCommentService postCommentService;

    private Grid<Post> postGrid;
    private Button postButton;
    private TextArea textArea;
    private User user;

    public DashboardView() {
        setId("dashboard-view");
        addClassName("dashboard-view");
        setMaxWidth("1366px");
        setHeightFull();

        createNewPost();
        createGrid();

        postButton.addClickListener(e -> {
            Post post = new Post();
            post.setText(textArea.getValue());
            post.setUser(user);

            postService.createPost(post);
            refreshGrid();
            textArea.clear();

            Notification.show("Saved");
        });
    }

    private void createNewPost() {
        HorizontalLayout newPost = new HorizontalLayout();
        newPost.addClassName("newPost");
        newPost.setSpacing(false);
        newPost.getThemeList().add("spacing-s");

        newPost.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        newPost.setWidthFull();

        textArea = new TextArea();
        textArea.setPlaceholder("Write something...");
        textArea.setAutofocus(true);
        textArea.setPreventInvalidInput(true);
        textArea.setMaxLength(500);

        textArea.setWidth("91%");
        textArea.setMaxHeight("5.15em");

        newPost.add(textArea);
        createPostButton(newPost);

        add(newPost);
    }

    private void createPostButton(HorizontalLayout newPost) {
        postButton = new Button();
        postButton.setText("POST");
        newPost.add(postButton);
    }

    private void createGrid() {
        postGrid = new Grid<>();

        postGrid.setHeight("80%");
        postGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS);
        postGrid.addComponentColumn(this::createCard);
        add(postGrid);
    }

    private HorizontalLayout createCard(Post userPost) {
        HorizontalLayout card = new HorizontalLayout();
        card.addClassName("card");
        card.setSpacing(false);
        card.getThemeList().add("spacing-s");

        Image image = new Image();
        image.setSrc(userPost.getUser().getUserAdditionalData().getPhotoURL());
        VerticalLayout description = new VerticalLayout();
        description.addClassName("description");
        description.setSpacing(false);
        description.setPadding(false);

        HorizontalLayout header = new HorizontalLayout();
        header.addClassName("header");
        header.setSpacing(false);
        header.getThemeList().add("spacing-s");

        Span name;
        if (userPost.getUser().getId().equals(this.user.getId())) {
            RouterLink routerLink = new <Long, UserView>RouterLink("@You", UserView.class, user.getId());
            name = new Span(routerLink);
        }
        else {
            RouterLink routerLink = new <Long, UserView>RouterLink("@" + userPost.getUser().getUserAdditionalData().getFirstName(), UserView.class, userPost.getUser().getId());
            name = new Span(routerLink);
        }
        name.addClassName("name");

        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        Span date = new Span(userPost.getDate().format(format));
        date.addClassName("date");
        header.add(name, date);

        Span post = new Span(userPost.getText());
        post.addClassName("post");

        var actions = createActionButtons(userPost);

        description.add(header, post, actions);
        card.add(image, description);

        return card;
    }

    private HorizontalLayout createActionButtons(Post userPost) {
        HorizontalLayout actions = new HorizontalLayout();
        actions.addClassName("actions");
        actions.setSpacing(false);
        actions.getThemeList().add("spacing-s");

        IronIcon likeIcon = new IronIcon("vaadin", "heart");
        Span likes = new Span(String.valueOf(userPost.getPostLikeList().size()));
        likes.addClassName("likes");

        if(postService.didILikeIt(userPost, user))
            likeIcon.setColor("red");

        IronIcon commentIcon = new IronIcon("vaadin", "comment");
        Span comments = new Span(String.valueOf(userPost.getCommentList().size()));
        comments.addClassName("comments");
        commentIcon.setId("comments");

        IronIcon shareIcon = new IronIcon("vaadin", "connect");
        Span shares = new Span(String.valueOf(userPost.getShares()));
        shares.addClassName("shares");

        // post content sharing
        String content = userPost.getUser().getUserAdditionalData().getFirstName() + "'s " +
                                  userPost.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) +
                                        "\n" + userPost.getText();
        shareIcon.setSize("1em"); // ClipboardHelper changes size
        shareIcon.setColor("#8d97a4"); // and color of icons
        ClipboardHelper clipboardHelper = new ClipboardHelper(content, shareIcon);

        likeIcon.addClickListener(event -> {
            if(likeIcon.getColor() != null && likeIcon.getColor().equals("red")){
                postService.iDontLikeIt(userPost, user);
                likeIcon.setColor("#8d97a4");
                Notification.show("You dont't like it");
            }
            else{
                postService.iLikeIt(userPost, user);
                likeIcon.setColor("red");
                Notification.show("You like it");
            }

            refreshGrid();
        });

        Dialog dialog = new Dialog();
        dialog.setOpened(false);
        dialog.setWidth("848px");
        dialog.setMaxHeight("80%");

        TextArea textArea = new TextArea();
        textArea.setAutofocus(true);
        textArea.setWidth("750px");
        textArea.setHeight("90px");
        textArea.setPlaceholder("Type smth...");
        textArea.setMaxLength(300);

        Button commentButton = new Button("POST");
        commentButton.getStyle().set("margin-left", "17px");

        commentIcon.addClickListener(event -> {
            textArea.clear();
            dialog.removeAll(); // cleaning
            dialog.add(textArea, commentButton, new H3());

            List<PostComment> postCommentList = postCommentService.findAllCommentsByPostId(userPost.getId()); //updating before display

            postCommentList.forEach(postComment -> {
                dialog.add(new H5( postComment.getUser().getUserAdditionalData().getFirstName() + "'s " + postComment.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));
                dialog.add(new Span(postComment.getText()));
                dialog.add(new H5());
            });
            dialog.open();
        });

        commentButton.addClickListener(event -> {
            PostComment postComment = new PostComment();
            postComment.setDate(LocalDateTime.now());
            postComment.setUser(user); // logged-in user
            postComment.setText(textArea.getValue());
            postCommentService.addCommentToPost(userPost, postComment);
            dialog.close();
            refreshGrid();
            Notification.show("Your comment has been saved");
        });

        shareIcon.addClickListener(event -> {
            postService.incrementShares(userPost);
            Notification.show("In your clipboard");
            refreshGrid();
        });

        actions.add(likeIcon, likes, commentIcon, comments, clipboardHelper, shares);

        return actions;
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        setThisUser();
        // Set some data when this view is displayed.
        List<Post> postList = postService.findAllByOrderByDateDesc();
        postGrid.setItems(postList);
    }

    private void refreshGrid() { // todo to improve
        Objects.requireNonNull(postGrid, "Grid cannot be null.");

        ListDataProvider<Post> dp = (ListDataProvider<Post>) postGrid.getDataProvider();
        dp.getItems().removeAll(((ListDataProvider<Post>) postGrid.getDataProvider()).getItems());
        dp.getItems().clear();

        dp.getItems().addAll(postService.findAllByOrderByDateDesc());
        dp.refreshAll();
    }

    private void setThisUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String principalEmail = ((UserDetails) principal).getUsername();
        userService.findUserByEmail(principalEmail).ifPresentOrElse(user1 -> this.user = user1, () -> {
            Notification.show("User cannot be set");
            logout();
        });
    }

    private void logout() {
        getUI().ifPresent(ui -> {
            // Close the VaadinServiceSession
            ui.getSession().close();

            // Invalidate underlying session instead if login info is stored there
             VaadinService.getCurrentRequest().getWrappedSession().invalidate();

            // Redirect to avoid keeping the removed UI open in the browser
            ui.getPage().setLocation(SecurityConfig.LOGOUT_SUCCESS_URL);
        });
    }
}
