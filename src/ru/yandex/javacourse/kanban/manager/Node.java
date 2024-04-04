package ru.yandex.javacourse.kanban.manager;

public class Node<E> {
    public E data;
    public Node<E> prev;
    public Node<E> next;

    public Node(Node<E> prev, E data, Node<E> next) {
        this.data = data;
        this.prev = prev;
        this.next = next;
    }

}
