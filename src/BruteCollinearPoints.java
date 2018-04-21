public class BruteCollinearPoints
{
  private int _numberOfSegments = 0;
  private LineSegment[] _lineSegments;


  private LineSegment getLineSegment(Point p0, Point p1, Point p2, Point p3)
  {
    Point minPoint = p0, maxPoint = p0;

    if (minPoint.compareTo(p1) > 0) { minPoint = p1; }
    if (minPoint.compareTo(p2) > 0) { minPoint = p2; }
    if (minPoint.compareTo(p3) > 0) { minPoint = p3; }

    if (maxPoint.compareTo(p1) < 0) { maxPoint = p1; }
    if (maxPoint.compareTo(p2) < 0) { maxPoint = p2; }
    if (maxPoint.compareTo(p3) < 0) { maxPoint = p3; }

    return new LineSegment(minPoint, maxPoint);
  }


  private void validateNonRepeatedPoints(Point p0, Point p1, Point p2, Point p3)
  {
    if ((p0.compareTo(p1) == 0) || (p0.compareTo(p2) == 0) || (p0.compareTo(p3) == 0) ||
        (p1.compareTo(p2) == 0) || (p1.compareTo(p3) == 0) || (p2.compareTo(p3) == 0))
    {
      throw new java.lang.IllegalArgumentException("Repeated points");
    }
  }
  // finds all line segments containing 4 points
  public BruteCollinearPoints(Point[] points_)
  {
    // some corner cases
    if (points_ == null)
    {
      throw new java.lang.IllegalArgumentException("Argument to ctr is null");
    }

    for (Point p : points_)
    {
      if (p == null)
      {
        throw new java.lang.IllegalArgumentException("Argument to ctr has a null Point");
      }
    }

    LineSegment[] lineSegments = new LineSegment[points_.length];

    for (int index0 = 0; index0 < points_.length; ++index0)
    {
      for (int index1 = index0 + 1; index1 < points_.length; ++index1)
      {
        for (int index2 = index1 + 1; index2 < points_.length; ++index2)
        {
          for (int index3 = index2 + 1; index3 < points_.length; ++index3)
          {
            // These points should be non-repeating
            validateNonRepeatedPoints(points_[index0], points_[index1], points_[index2], points_[index3]);

            double slop01 = points_[index0].slopeTo(points_[index1]);
            double slop02 = points_[index0].slopeTo(points_[index2]);

            if (slop01 != slop02) continue;

            double slop03 = points_[index0].slopeTo(points_[index3]);
            if (slop01 == slop03)
            {
              lineSegments[_numberOfSegments++] = getLineSegment(points_[index0],points_[index1],
                                                                  points_[index2], points_[index3]);
            }
          }
        }
      }
    }

    _lineSegments = new LineSegment[_numberOfSegments];
    for (int index = 0; index < _numberOfSegments; ++index)
    {
      _lineSegments[index] = lineSegments[index];
      lineSegments[index] = null;
    }
  }

  // the number of line segments
  public int numberOfSegments()
  {
    return _numberOfSegments;
  }

  // the line segments
  public LineSegment[] segments()
  {
    return _lineSegments;
  }
}
