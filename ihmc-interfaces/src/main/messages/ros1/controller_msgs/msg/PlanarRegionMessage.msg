int32 NO_REGION_ID=-1

# Unique ID used to identify this message, should preferably be consecutively increasing.
uint32 sequence_id

# Field default value -1
int32 region_id

geometry_msgs/Point region_origin

geometry_msgs/Vector3 region_normal

geometry_msgs/Point[] vertex_buffer

int32 concave_hull_size

int32 number_of_convex_polygons

int32[] convex_polygons_size


