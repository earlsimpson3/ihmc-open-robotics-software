# This message is part of the IHMC robot environment awareness module.
# This message contains a single planar region.
# It is defined by an origin, a normal, and a concave hull.
# For easier processing, a set of convex polygons approximating the concave hull are also provided.

# Unique ID used to identify this message, should preferably be consecutively increasing.
uint32 sequence_id

int32 NO_REGION_ID = -1

int32 region_id -1
geometry_msgs/Point region_origin
geometry_msgs/Vector3 region_normal

geometry_msgs/Point[<=1000] vertex_buffer
int32 concave_hull_size
int32 number_of_convex_polygons
int32[<=20] convex_polygons_size
