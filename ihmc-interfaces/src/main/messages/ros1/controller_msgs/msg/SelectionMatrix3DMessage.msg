# Unique ID used to identify this message, should preferably be consecutively increasing.
uint32 sequence_id

# The ID of the reference frame defining the selection frame.
# When selecting the axes of interest, these axes refer to the selection frame axes.
# This frame is optional. It is preferable to provide it when possible, but when it is absent, i.e. equal to 0,
# the selection matrix will then be generated regardless to what frame is it used in.
int64 selection_frame_id

# Specifies whether the x-axis of the selection frame is an axis of interest.
# Field default value True
bool x_selected

# Specifies whether the y-axis of the selection frame is an axis of interest.
# Field default value True
bool y_selected

# Specifies whether the z-axis of the selection frame is an axis of interest.
# Field default value True
bool z_selected


