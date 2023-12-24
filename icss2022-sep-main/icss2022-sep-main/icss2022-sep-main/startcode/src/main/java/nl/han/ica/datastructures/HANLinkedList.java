package nl.han.ica.datastructures;


import java.util.Iterator;

public class HANLinkedList<T> implements IHANLinkedList<T>{
//ALL CODE Checker
    private Node<T> head;
    private int size;

    public HANLinkedList(){
        head = null;
        size = 0;
    }
    @Override
    public void addFirst(T value) {
        HANLinkedList.Node<T> node = new HANLinkedList.Node<>(value);
        node.setNext(head);
        head = node;
        size++;
    }

    @Override
    public void clear() {
        if (head != null) {
            head.next = null;
        }
        // Eventueel kun je ook de `head` zelf op `null` zetten om de lijst leeg te maken:
        // head = null;
    }
    @Override
    public void insert(int index, T value) {
        if (index < 0) {
            throw new IllegalArgumentException("Index cannot be negative.");
        }

        Node<T> newNode = new Node<>(value);

        if (index == 0) {
        addFirst(value);
        } else {
            Node<T> current = head;
            int i = 0;

            while (i < index - 1 && current != null) {
                current = current.next;
                i++;
            }

            if (current == null) {
                throw new IndexOutOfBoundsException("Index is out of bounds.");
            }

            newNode.next = current.next;
            current.next = newNode;
        }
    }

    @Override
    public void delete(int pos) {
            if (pos < 0) {
                throw new IllegalArgumentException("Position cannot be negative.");
            }

            if (pos == 0) {
                // Delete the head node
                if (head != null) {
                    head = head.next;
                } else {
                    throw new IndexOutOfBoundsException("List is empty.");
                }
            } else {
                Node<T> current = head;
                int i = 0;

                while (i < pos - 1 && current != null) {
                    current = current.next;
                    i++;
                }

                if (current == null || current.next == null) {
                    throw new IndexOutOfBoundsException("Index is out of bounds.");
                }

                current.next = current.next.next;
            }
        }

    public T get(int pos) {
        if (pos < 0 || pos >= size) {
            throw new IndexOutOfBoundsException("Index is out of bounds.");
        }

        Node<T> current = head;
        int index = 0;
        while (current != null) {
            if(index == pos) {
                return current.data;
            }
            current = current.next;
            index++;
        }

        return null;
    }



    public void removeFirst() {
        if (head != null) {
            head = head.next;
        }
    }

    @Override
    public T getFirst() {
        if (head != null) {
            return head.data;
        } else {
            return null;
        }
    }

    @Override
    public int getSize() {
        return size;
    }


    public boolean isEmpty() {
        return size == 0;
    }

    private static class Node<T> {


        private HANLinkedList.Node<T> next;

        private T data;

        public Node(T data) {
            this.data = data;
        }

        public T getData() {
            return data;
        }

        public void setData(){
            this.data = data;
        }

        public HANLinkedList.Node<T> getNext() {
            return next;
        }

        public void setNext(HANLinkedList.Node<T> next){
            this.next = next;
        }

        public String toString(){
            return "Node{" +
                    "data=" + data.getClass().getSimpleName() +
                    ", next=" + next +
                    '}';
        }
    }
}
