import java.util.Iterator;
import net.runelite.mapping.Export;
import net.runelite.mapping.Implements;
import net.runelite.mapping.ObfuscatedName;
import net.runelite.mapping.ObfuscatedSignature;

@ObfuscatedName("gs")
@Implements("CombatInfoList")
public class CombatInfoList implements Iterable {
   @ObfuscatedName("n")
   @ObfuscatedSignature(
      signature = "Lgw;"
   )
   @Export("node")
   Node node;
   @ObfuscatedName("v")
   @ObfuscatedSignature(
      signature = "Lgw;"
   )
   @Export("current")
   Node current;

   public CombatInfoList() {
      this.node = new Node();
      this.node.next = this.node;
      this.node.previous = this.node;
   }

   @ObfuscatedName("n")
   public void method3786() {
      while(this.node.next != this.node) {
         this.node.next.unlink();
      }

   }

   @ObfuscatedName("v")
   @ObfuscatedSignature(
      signature = "(Lgw;)V"
   )
   @Export("addFirst")
   public void addFirst(Node var1) {
      if(var1.previous != null) {
         var1.unlink();
      }

      var1.previous = this.node.previous;
      var1.next = this.node;
      var1.previous.next = var1;
      var1.next.previous = var1;
   }

   @ObfuscatedName("y")
   @ObfuscatedSignature(
      signature = "(Lgw;)V"
   )
   @Export("addLast")
   public void addLast(Node var1) {
      if(var1.previous != null) {
         var1.unlink();
      }

      var1.previous = this.node;
      var1.next = this.node.next;
      var1.previous.next = var1;
      var1.next.previous = var1;
   }

   @ObfuscatedName("h")
   @ObfuscatedSignature(
      signature = "()Lgw;"
   )
   @Export("removeLast")
   public Node removeLast() {
      Node var1 = this.node.next;
      if(var1 == this.node) {
         return null;
      } else {
         var1.unlink();
         return var1;
      }
   }

   @ObfuscatedName("d")
   @ObfuscatedSignature(
      signature = "()Lgw;"
   )
   @Export("last")
   public Node last() {
      return this.previousOrLast((Node)null);
   }

   @ObfuscatedName("s")
   @ObfuscatedSignature(
      signature = "(Lgw;)Lgw;"
   )
   @Export("previousOrLast")
   Node previousOrLast(Node var1) {
      Node var2;
      if(var1 == null) {
         var2 = this.node.next;
      } else {
         var2 = var1;
      }

      if(var2 == this.node) {
         this.current = null;
         return null;
      } else {
         this.current = var2.next;
         return var2;
      }
   }

   @ObfuscatedName("b")
   @ObfuscatedSignature(
      signature = "()Lgw;"
   )
   @Export("previous")
   public Node previous() {
      Node var1 = this.current;
      if(var1 == this.node) {
         this.current = null;
         return null;
      } else {
         this.current = var1.next;
         return var1;
      }
   }

   @ObfuscatedName("e")
   @Export("isEmpty")
   public boolean isEmpty() {
      return this.node.next == this.node;
   }

   public Iterator iterator() {
      return new class199(this);
   }

   @ObfuscatedName("r")
   @ObfuscatedSignature(
      signature = "(Lgw;Lgw;)V"
   )
   public static void method3766(Node var0, Node var1) {
      if(var0.previous != null) {
         var0.unlink();
      }

      var0.previous = var1;
      var0.next = var1.next;
      var0.previous.next = var0;
      var0.next.previous = var0;
   }
}
