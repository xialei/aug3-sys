#!/bin/bash
# ==============================================================================
#  Causes server to reload its configuration
# ==============================================================================
exec `dirname $0`/djava.sh com.hp.sys.cfg.UpdateNotifier $@
