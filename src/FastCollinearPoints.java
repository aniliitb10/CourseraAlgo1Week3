import java.util.Arrays;
import java.util.ArrayList;

public class FastCollinearPoints {
  private ArrayList<LineSegment> _lineSegments = new ArrayList<>();

  private class FCPoint implements Comparable<FCPoint>
  {
    private Point _p;
    private double _slopeWithOrigin;
    public FCPoint(Point p_, double slopeWithOrigin_)
    {
      if (p_ == null)
      {
        throw new java.lang.IllegalArgumentException("Argument to ctr is null");
      }
      this._p = p_;
      this._slopeWithOrigin = slopeWithOrigin_;
    }

    double slope() { return this._slopeWithOrigin; }

    Point getPoint() { return this._p; }

    @Override
    public int compareTo(FCPoint p_) {
      return (Double.compare(this._slopeWithOrigin, p_._slopeWithOrigin));
    }
  }

  private class LineSegmentPoints
  {
    private Point point1;
    private Point point2;

    LineSegmentPoints(Point p1_, Point p2_)
    {
      if((p1_ == null) || (p2_ == null))
      {
        throw new java.lang.IllegalArgumentException("Argument to ctr is null");
      }

      this.point1 = p1_;
      this.point2 = p2_;
    }

    double slope()
    {
      return this.point1.slopeTo(point2);
    }

    LineSegment getLineSegment()
    {
      return new LineSegment(this.point1, this.point2);
    }

    boolean collinearPoints(Point p1_, Point p2_)
    {
      if (this.slope() != p1_.slopeTo(p2_)) return false;

      if ((this.point1.compareTo(p1_) != 0) && (this.point1.compareTo(p2_) != 0))
      {
        return (this.point1.slopeOrder().compare(p1_, p2_) == 0);
      }
      else return true; // slopes are same and a point is common
    }
  }

  private LineSegmentPoints getLineSegmentPoints(FCPoint[] fcPoints_, int beginIndex_, int endIndex_, Point origin_)
  {
    Point least = origin_;
    Point max   = origin_;

    for (int index = beginIndex_; index < endIndex_; ++index)
    {
      least = (fcPoints_[index].getPoint().compareTo(least) < 0 ? fcPoints_[index].getPoint() : least);
      max   = (fcPoints_[index].getPoint().compareTo(max)   > 0 ? fcPoints_[index].getPoint() : max  );
    }
    return new LineSegmentPoints(least, max);
  }

  // finds all line segments containing 4 or more points
  public FastCollinearPoints(Point[] points_)
  {
    // some corner cases
    if (points_ == null)
    {
      throw new IllegalArgumentException("Argument to ctr is null");
    }

    for (Point p : points_)
    {
      if (p == null)
      {
        throw new IllegalArgumentException("Argument to ctr has a null Point");
      }
    }

    Arrays.sort(points_); // just to make sure that 1st line segment is the largest one
    LineSegmentPoints[] lineSegmentPoints = new LineSegmentPoints[points_.length];
    int numberOfSegments = 0;

    for (int index = 0; index < points_.length; ++index)
    {
      int counter = 0;
      FCPoint[] fcPoints = new FCPoint[points_.length - index - 1];
      for (int otherIndex = (index + 1); otherIndex < points_.length; ++otherIndex)
      {
        fcPoints[counter++] = new FCPoint(points_[otherIndex], points_[index].slopeTo(points_[otherIndex]));
      }
      Arrays.sort(fcPoints);

      // find if any 3 consecutive points have same slope
      for (int fcPointsIndex = 0; fcPointsIndex < (fcPoints.length - 2); ++fcPointsIndex)
      {
        if (fcPoints[fcPointsIndex].slope() == fcPoints[fcPointsIndex + 2].slope())
        {
          // find the maximum number of such points in the list
          int sameFcPointsIndex = (fcPointsIndex + 2);
          boolean reachedEnd = true;
          for (; sameFcPointsIndex < (fcPoints.length); ++sameFcPointsIndex)
          {
            // stop when either the slopes are no longer equal or all points are on the line segment
            if (fcPoints[fcPointsIndex].slope() != fcPoints[sameFcPointsIndex].slope())
            {
              reachedEnd = false;
              break;
            }
          }

          //check if this line is not already in the list
          boolean isCollinear = false;
          for (int lspCounter = 0; lspCounter < numberOfSegments; ++lspCounter)
          {
            if (lineSegmentPoints[lspCounter].slope() == fcPoints[fcPointsIndex].slope())
            {
              Point p1 = fcPoints[fcPointsIndex].getPoint();
              Point p2 = fcPoints[fcPointsIndex + 1].getPoint();
              if (lineSegmentPoints[lspCounter].collinearPoints(p1, p2))
              {
                isCollinear = true;
                break;
              }
            }
          }

          if (!isCollinear)
          {
            lineSegmentPoints[numberOfSegments++] = getLineSegmentPoints(fcPoints, fcPointsIndex, (reachedEnd ?  fcPoints.length : sameFcPointsIndex), points_[index]);
          }
          fcPointsIndex = (reachedEnd ?  fcPoints.length : sameFcPointsIndex - 1);
        }
      } // done for one origin
    } // done for all points

    for (int finalIndex = 0; finalIndex < numberOfSegments; ++finalIndex)
    {
      _lineSegments.add(lineSegmentPoints[finalIndex].getLineSegment());
      lineSegmentPoints[finalIndex] = null;
    }
  }
  // the number of line segments
  public int numberOfSegments()
  {
    return _lineSegments.size();
  }

  // the line segments
  public LineSegment[] segments()
  {
    return _lineSegments.toArray(new LineSegment[0]);
  }
}