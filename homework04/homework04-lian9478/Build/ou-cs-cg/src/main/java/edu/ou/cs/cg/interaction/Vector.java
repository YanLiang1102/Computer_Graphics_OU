
   package edu.ou.cs.cg.interaction;
   public class Vector {

   protected float dX;
   protected float dY;

   // Constructor methods ....
   public Vector() {
      dX = dY = 0.0f;
   }

   public Vector( float dX, float dY ) {
      this.dX = dX;
      this.dY = dY;
   }

   // Convert vector to a string ...
    
   public String toString() {
      return "Vector(" + dX + ", " + dY + ")";
   }

   // Compute magnitude of vector ....
 
   public float length() {
      return (float)Math.sqrt ( dX*dX + dY*dY );
   }

   // Sum of two vectors ....

   public Vector add( Vector v1 ) {
       Vector v2 = new Vector( this.dX + v1.dX, this.dY + v1.dY );
       return v2;
   }

   // Subtract vector v1 from v .....

   public Vector subv( Vector v1 ) {
       Vector v2 = new Vector( this.dX - v1.dX, this.dY - v1.dY );
       return v2;
   }

   // Scale vector by a constant ...

   public Vector scale( float scaleFactor ) {
       Vector v2 = new Vector( this.dX*scaleFactor, this.dY*scaleFactor );
       return v2;
   }

   // Normalize a vectors length....

   public Vector normalize() {
      Vector v2 = new Vector();

      float length =(float)Math.sqrt( this.dX*this.dX + this.dY*this.dY );
      if (length != 0) {
        v2.dX = this.dX/length;
        v2.dY = this.dY/length;
      }

      return v2;
   }   

   // Dot product of two vectors .....

   public float dotProduct ( Vector v1 ) {
        return this.dX*v1.dX + this.dY*v1.dY;
   }
}