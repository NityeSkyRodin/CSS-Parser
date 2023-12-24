package nl.han.ica.datastructures;

public class HANStack<T> implements IHANStack<T> {
// ALL CODE PA00-PA04
    private Node<T> head;
    private int size;

    public HANStack() {
        head = null;
        size = 0;
    }


    @Override
    public void push(T value) {
        Node<T> node = new Node<>(value);
        node.setNext(head);
        head = node;
        size++;
    }

    @Override
    public T pop() {
        if(head == null){
            throw new IllegalStateException("Stack is empty");
        }

        T data = head.getData();
        head = head.getNext();
        size--;
        return data;
    }

    @Override
    public T peek() {
        if(isEmpty()){
            throw new IllegalStateException("Stack is empty");
        }
        return head.data;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public String toString(){
        return "HANSStack{" +
                "head=" + (head != null ? head : null) +
                ", size=" + size +
                '}';
    }
    static class Node<T> {
        private Node<T> next;

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

        public Node<T> getNext() {
            return next;
        }

        public void setNext(Node<T> next){
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
