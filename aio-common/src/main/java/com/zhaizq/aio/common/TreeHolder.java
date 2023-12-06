package com.zhaizq.aio.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TreeHolder<T> {
    final List<TreeNode<T>> list;
    final Map<Object, TreeNode<T>> map;

    public TreeHolder(Function<T, Object> getId, Function<T, Object> getPid, List<T> data) {
        this(data.stream().map(v -> new TreeNode<>(getId.apply(v), getPid.apply(v), v)).collect(Collectors.toList()));
    }

    public TreeHolder(List<TreeNode<T>> list) {
        Map<Object, List<TreeNode<T>>> childrenMap = new HashMap<>();
        list.forEach(v -> childrenMap.computeIfAbsent(v.getPid(), k -> new LinkedList<>()).add(v));
        list.forEach(v -> v.setChildren(childrenMap.getOrDefault(v.getId(), Collections.emptyList())));
        list.forEach(v -> v.getChildren().forEach(child -> child.setParent(v)));

        this.list = Collections.unmodifiableList(list);
        this.map = Collections.unmodifiableMap(list.stream().collect(Collectors.toMap(TreeNode::getId, v -> v)));
    }

    public T getById(Object id) {
        TreeNode<T> tTreeNode = map.get(id);
        return tTreeNode == null ? null : tTreeNode.getData();
    }

    public List<T> getParentsById(Object id) {
        LinkedList<T> objects = new LinkedList<>();
        for (TreeNode<T> tTreeNode = map.get(id); tTreeNode != null; tTreeNode = tTreeNode.getParent())
            objects.addFirst(tTreeNode.getData());
        return objects;
    }

    public List<LabelNode> labelTree(Function<T, Object> getLabel) {
        List<LabelNode> nodes = new LinkedList<>();
        Map<Object, LabelNode> map = new HashMap<>();
        for (TreeNode<T> item : list) {
            Object label = getLabel.apply(item.getData());
            if (label == null) continue;

            LabelNode labelNode = new LabelNode(item.getId(), label);
            if (item.getParent() == null)
                nodes.add(labelNode);

            LabelNode parent = map.get(item.getPid());
            if (parent != null)
                parent.getChildren().add(labelNode);
            map.put(item.getId(), labelNode);
        }

        return nodes;
    }

    @Getter @Setter
    @RequiredArgsConstructor
    public static class TreeNode<T> {
        private final Object id;
        private final Object pid;
        private final T data;
        private TreeNode<T> parent;
        private List<TreeNode<T>> children;
    }

    @Getter @Setter
    @RequiredArgsConstructor
    public static class LabelNode {
        private final Object id;
        private final Object label;
        private final List<LabelNode> children = new LinkedList<>();
    }
}