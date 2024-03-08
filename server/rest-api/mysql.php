<?php
    class MySql implements Db {
        private $host;
        private $user;
        private $password;
        private $dbname;
        private $userId;
        private $identityProviderId;
        private $contentDir;

        public function __construct($host, $user, $password, $dbname, $userid = null, $identityproviderid = null, $contentdir = null) {
            $this->host = $host;
            $this->user = $user;
            $this->password = $password;
            $this->dbname = $dbname;
            $this->userId = $userid;
            $this->identityProviderId = $identityproviderid;
            if (is_null($contentdir)) {
                $this->contentDir = substr(__FILE__, 0, strrpos(__FILE__, '/')+1) . "content";
            }
            else {
                $this->contentDir = $contentdir;
            }
            if (!is_dir($this->contentDir))
                mkdir($this->contentDir, 0777, true);
        }
        
        public function getStores() {
            try {
                $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
                $readRight = $conn->query("select level from rights where systemright='read'");
                if ($right = $readRight->fetch_object()) {
                    $storeresults = $conn->query("select id, name, datecreated, creator, creatoridentityproviderid, lastmodifier, lastmodifieddate, lastmodifieridentityproviderid from stores where hasStoreRight(id,'" . $this->userId . "','" . $this->identityProviderId . "'," . $right->level . ")");
                    $stores = [];
                    while ($row = $storeresults->fetch_object()) {
                        $newStore = new Store($row->id, $row->name);
                        $newStore->setDateCreated(($row->datecreated));
                        $newStore->setCreator($row->creator);
                        $newStore->setCreatorIdentityProviderId($row->creatoridentityproviderid);
                        $newStore->setLastModified($row->lastmodifieddate);
                        $newStore->setLastModifier($row->lastmodifier);
                        $newStore->setLastModifierIdentityProviderId($row->lastmodifieridentityproviderid);
                        $stores[sizeof($stores)] = $newStore;
                        //Register get store event
                        $conn->query("insert into events (type,sourceid, userid, identityproviderid) values ('store.get', '" . $row->id . "','" . $this->userId . "','" . $this->identityProviderId . "')");
                    }
                    return $stores;
                }
                else
                    return [];
            }
            finally {
                $conn->close();
            }
        }

        public function canGetStore($id) {
            try {
                $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
                $readRight = $conn->query("select level from rights where systemright='read'");
                if ($readRight && $readRight->num_rows == 1) {
                    $storeidresult = $conn->query("select id from stores where id = '". $id . "' or name = '" . $id . "'");
                    if ($storeidresult->num_rows == 1) {
                        $id = $storeidresult->fetch_object()->id;
                        $storeresult = $conn->query("select hasStoreRight('" . $id . "','" . $this->userId . "','" . $this->identityProviderId . "'," . $readRight->fetch_object()->level . ") hasright from stores where (id = '" . $id . "' or name = '" . $id . "')");
                        if ($storeresult && $storeresult->num_rows == 1) {
                            if ($storeresult->fetch_object()->hasright == 1)
                                return true;
                            else
                                return false;
                        }
                    }
                    else {
                        return false;
                    }
                }
                else {
                    return false;
                }
            }
            finally {
                $conn->close();
            }
        }

        public function getStore($id) {
            try {
                $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
                $readRight = $conn->query("select level from rights where systemright='read'");
                if ($right = $readRight->fetch_object()) {
                    $storeresult = $conn->query("select id, name, datecreated, creator, creatoridentityproviderid, lastmodifier, lastmodifieddate, lastmodifieridentityproviderid from stores where hasStoreRight(id, '" . $this->userId . "','" . $this->identityProviderId . "'," . $right->level . ") and (id = '" . $id . "' or name = '" . $id . "')");
                    if ($storeresult) {
                        if ($row = $storeresult->fetch_object()) {
                            $store = new Store($row->id, $row->name);
                            $store->setDateCreated(($row->datecreated));
                            $store->setCreator($row->creator);
                            $store->setCreatorIdentityProviderId($row->creatoridentityproviderid);
                            $store->setLastModified($row->lastmodifieddate);
                            $store->setLastModifier($row->lastmodifier);
                            $store->setLastModifierIdentityProviderId($row->lastmodifieridentityproviderid);

                            //Add rights
                            $rights = $conn->query("select granteeid, granteetype, identityproviderid,level,weight from grantedrights where targettype = 'store' and targetid = '" . $id . "'");
                            while ($grantedright = $rights->fetch_object()) {
                                $newRight = new GrantedRight($grantedright->granteeid, $grantedright->granteetype, $grantedright->identityproviderid, $grantedright->level, $grantedright->weight);
                                $store->addRight($newRight);
                            }

                            //Register get object event
                            $conn->query("insert into events (type,sourceid, userid, identityproviderid) values ('store.get', '" . $id . "','" . $this->userId . "','" . $this->identityProviderId . "')");
                            return $store;
                        }
                        else
                            return false;
                    }
                    else
                        return false;
                }
                else
                    return false;
            }
            finally {
                $conn->close();
            }        
        }


        public function getIdentityProviders() {
            try {
                $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
                $rows = $conn->query("select id, name, `type` from identityproviders");
                $providers = [];
                while ($row = $rows->fetch_object()) {
                    $providers[sizeof($providers)] = new IdentityProvider($row->id, $row->name, $row->type);
                }
                return $providers;
            }
            finally {
                $conn->close();
            }
        }

        public function getIdentityProvider($id) {
            try {
                $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
                $idps = $conn->query("select id,name,rolescache, `type` from identityproviders where id = '" . $id . "'");

                if ($idp = $idps->fetch_object()) {
                    $result = new IdentityProvider($idp->id, $idp->name, $idp->type);
                    $result->setRolesCache($idp->rolescache);

                    $idpprops = $conn->query("select property, value from identityproviderproperties where identityproviderid = '" . $id . "'");
                    while ($prop = $idpprops->fetch_object()) {
                        $result->addSetting($prop->property, $prop->value);
                    }
                    return $result;
                }
                else
                    return false;
            }
            finally {
                $conn->close();
            }
        }

        public function getIdentifyProviders() {
            try {
                $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
                $idps = $conn->query("select id,name, `type` from identityproviders");
                $results = [];
                while ($idp = $idps->fetch_object()) {
                    $results[sizeof($results)] = new IdentityProvider($idp->id, $idp->name, $idp->type);
                }

                $idpprops = $conn->query("select property, value, identityproviderid from identityproviderproperties");
                while ($prop = $idpprops->fetch_object()) {
                    foreach($results as $idp) {
                        if ($idp->getId() == $prop->identityproviderid) {
                            $idp->addSetting($prop->property, $prop->value);
                        }
                    }
                }
                return $results;
            }
            finally {
                $conn->close();
            }
        }

        public function getInternalUser($mail, $passwordhash, $idpid) {
            try {
                $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
                $users = $conn->query("select id,firstname,lastname from users where email='" . $mail ."' and passwordhash='" . $passwordhash ."'");
                if ($user = $users->fetch_object()) {
                    return new User($user->id, $mail, $user->firstname, $user->lastname, $idpid);
                }
                else {
                    return false;
                }
            }
            finally {
                $conn->close();
            }
        }

        public function getInternalUserById($id) {
            try {
                $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
                $users = $conn->query("select id,firstname,lastname from users where id='" . $id ."'");
                if ($user = $users->fetch_object()) {
                    return new User($user->id, $mail, $user->firstname, $user->lastname, $idpid);
                }
                else {
                    return false;
                }
            }
            finally {
                $conn->close();
            }
            return false;
        }

        public function isStoreNameUnique($name) {
            try {
                $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
                $results = $conn->query("select id from stores where name='" . $name . "'");
                return $results->num_rows == 0;
            }
            finally {
                $conn->close();
            }
        }

        public function isUniqueClassName($storeid, $classname) {
            try {
                $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
                $results = $conn->query("select id from classes where name='" . $classname . "' and storeid = '" . $storeid . "'");
                return $results->num_rows == 0;
            }
            finally {
                $conn->close();
            }
        }

        public function canCreateStore() {
            try {
                $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
                $rights = $conn->query("select level from rights where systemright='createstore'");
                if ($rights->num_rows == 1) {
                    $results = $conn->query("select hasDomainRight('" . $this->userId . "','" . $this->identityProviderId . "'," . $rights->fetch_object()->level . ") hasright from rights LIMIT 1");
                    if ($results && $results->num_rows == 1) {
                        return $results->fetch_object()->hasright == 1;
                    }
                    else {
                        echo 'Unable to create store';
                        return false;
                    }
                }
                else {
                    return false;
                }
            }
            finally {
                $conn->close();
            }
        }

        public function canDeleteStore($storeid) {
            try {
                $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
                $rights = $conn->query("select level from rights where systemright='delete'");
                if ($rights->num_rows == 1) {
                    $right = $rights->fetch_object();
                    $results = $conn->query("select hasStoreRight(id,'" . $this->userId. "','" . $this->identityProviderId . "'," . $right->level . ") hasright from stores where id = '" . $storeid . "'");
                    if ($results->num_rows == 1) {
                        return $result->fetch_object()->hasright == 1;
                    }
                    else {
                        return false;
                    }
                }
                else {
                    return false;
                }
            }
            finally {
                $conn->close();
            }
        }

        public function canDeleteObject($storeid, $objectid) {
            try {
                $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
                $rights = $conn->query("select level from rights where systemright='delete'");
                if ($rights->num_rows == 1) {
                    $right = $rights->fetch_object();
                    $results = $conn->query("select hasObjectRight(id,'" . $this->userId . "','" . $this->identityProviderId . "'," . $right->level . ") hasright from objects where id = '" . $objectid . "'");
                    if ($results->num_rows == 1) {
                        return $results->fetch_object()->hasright == 1;
                    }
                    else {
                        return false;
                    }
                }
                else {
                    return false;
                }
            }
            finally {
                $conn->close();
            }
        }

        public function canCreateClass($storeid) {
            try {
                $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
                $rights = $conn->query("select level from rights where systemright='createclass'");
                $right = $rights->fetch_object();
                echo "select hasStoreRight('" . $storeid . "','" . $this->userId . "','" . $this->identityProviderId . "'," . $right->level . ") hasright from rights LIMIT 1";
                $results = $conn->query("select hasStoreRight('" . $storeid . "','" . $this->userId . "','" . $this->identityProviderId . "'," . $right->level . ") hasright from rights LIMIT 1");
                if ($results->num_rows == 1) {
                    if ($result = $results->fetch_object()) {
                        return ($result->hasright == 1);
                    }
                    else {
                        return false;
                    }
                }
                else {
                    return false;
                }
            }
            finally {
                $conn->close();
            }
        }

        public function createStore($name, $grantedrights, $addons) {
            try {
                $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
                $idquery = $conn->query("select UUID() newid from INFORMATION_SCHEMA.TABLES LIMIT 1");
                $id = $idquery->fetch_object()->newid;
                $conn->begin_transaction();
                try {
                    $succeeded = true;
                    $succeeded = $conn->query("insert into stores (id, name, creator, creatoridentityproviderid, lastmodifier, lastmodifieridentityproviderid, lastmodifieddate) values ('" . $id . "','" . $name . "','" . $this->userId . "','" . $this->identityProviderId . "','" . $this->userId . "','" . $this->identityProviderId . "', CURRENT_TIMESTAMP())");
                    if ($succeeded) {
                        if (sizeof($grantedrights) > 0) {
                            foreach($grantedrights as $right) {
                                if ($right->grantee == 'everyone') {
                                    $succeeded = $conn->query("insert into grantedrights (granteeid, granteetype, targetid, targettype, level, weight) values ('everyone', 'special', '" . $id . "','store'," . $right->level . ",0)");
                                }
                                else if ($right->granteetype == 'user') {
                                    $succeeded = $conn->query("insert into grantedrights (granteeid, granteetype, identityproviderid, targetid, targettype, level, weight) values ('" . $right->grantee . "','user','" . $right->identityprovider . "','" . $id . "','store'," . $right->level . ",". $this->getGranteeTypeWeigth('user') . ")");
                                }
                                else if ($right->granteetype == 'role') {
                                    $succeeded = $conn->query("insert into grantedrights (granteeid, granteetype, identityproviderid, targetid, targettype, level, weight) values ('" . $right->grantee . "','role','" . $right->identityprovider . "','" . $id . "','store'," . $right->level . ",". $this->getGranteeTypeWeigth('role') . ")");
                                }
                                else {
                                    $succeeded = false;
                                }
                                if (!$succeeded)
                                    break;
                            }
                        }
                        else {
                            $succeeded = $conn->query("insert into grantedrights (granteeid, granteetype, identityproviderid, targetid, targettype, level, weight) (select granteeid, granteetype, identityproviderid,'" . $id . "',targettype, level, weight from grantedrights where targettype = 'store' and targetid IS NULL)");
                            if ($succeeded) {
                                $countres = $conn->query("select count(*) countedrights from grantedrights where targetid = '" . $id . "'");
                                $rightscount = $countres->fetch_object()->countedrights;
                                if ($rightscount == 0) {
                                    $succeeded = $conn->query("insert into grantedrights (granteeid, granteetype, identityproviderid, targetid, targettype, level, weight) values ('" . $this->userId . "','user','" . $this->identityProviderId . "','" . $id . "','store', (select sum(level) from rights where storeright=1), 1)");
                                }
                            }
                        }
                        if ($addons) {
                            foreach($addons as $addon) {
                                $jsons = $conn->query("select structure from addons where id='" . $addon . "'");
                                if ($json = $jsons->fetch_object()) {
                                    $definition = json_decode($json->structure);
                                    foreach($definition->classes as $classdefinition) {
                                        $class = AdaClass::fromAddOn($classdefinition);
                                        if ($classdefinition->security) {
                                            $rights = $this->getRights();
                                            foreach($classdefinition->security as $sec) {
                                                $level = 0;
                                                foreach($sec->rights as $right) {
                                                    foreach($rights as $checkright) {
                                                        if ($checkright->getName() == $right || $checkright->getSystemRight() == $right)
                                                            $level += $checkright->getLevel();
                                                    }
                                                }
                                            }
                                            if ($sec->grantee == 'everyone') {
                                                $right = new GrantedRight('everyone', 'special', null, $level, 0);
                                                $class->addRight($right);
                                            }
                                            else {
                                                $right = new GrantedRight($sec->grantee, 'user', $sec->identityprovider, $level, 1);
                                                $class->addRight($right);
                                            }
                                        }
                                        $idquery = $conn->query("select UUID() newid from INFORMATION_SCHEMA.TABLES LIMIT 1");
                                        $classid = $idquery->fetch_object()->newid;
                                        $conn->query("insert into classes (id, name, creator, creatoridentityproviderid, folderclass, contentclass, storeid) values ('" . $classid . "','" . $class->getName() . "','" . $this->userId . "','" . $this->identityProviderId . "'," . ($class->isFolderClass() ? '1' : '0') . "," . ($class->isDocumentClass() ? '1': '0') . ",'" . $id . "')");
                        
                                        foreach($class->getProperties() as $property) {
                                            if (is_null($property->getValueClass()))
                                                $conn->query("insert into classproperties (id, name, `type`, required, multiple, classid) values (UUID(), '" . $property->getName() . "','" . $property->getType() . "'," . ($property->isRequired()? 1:0) . "," . ($property->isMultiple()? 1: 0). ",'" . $classid . "')");
                                            else if ($property->getValueClass() == 'ThisClass')
                                                $conn->query("insert into classproperties (id, name, `type`, required, multiple, classid, objectclassid) values (UUID(), '" . $property->getName() . "','" . $property->getType() . "'," . ($property->isRequired()? 1:0) . "," . ($property->isMultiple()? 1: 0). ",'" . $classid . "','" . $classid . "')");
                                            else {
                                                $classresults = $conn->query("select id from classes where id = '" . $property->getValueClass() . "' or name='" . $property->getValueClass() . "'");
                                                if ($classresults) {
                                                    $conn->query("insert into classproperties (id, name, `type`, required, multiple, classid, objectclassid) values (UUID(), '" . $property->getName() . "','" . $property->getType() . "'," . ($property->isRequired()? 1:0) . "," . ($property->isMultiple()? 1: 0). ",'" . $classid . "','" . $classresults->fetch_object()->id . "')");
                                                }
                                                else
                                                    $succeeded = false;
                                            }
                                        }
                        
                                        if (sizeof($class->getRights()) == 0) {
                                            $conn->query("insert into classrights (granteeid, granteetype, identityproviderid, classid, level, weight) values ('" . $this->userId . "','user','" . $this->identityProviderId . "','". $classid . "',(select sum(level) from rights where classright = 1), 1)");
                                        }
                                        else {
                                            foreach($class->getRights() as $right) {
                                                if ($right->getGranteeId() == 'everyone') {
                                                    $conn->query("insert into classrights (granteeid, granteetype, classid, level, weight) values ('everyone', 'special', '" . $classid . "'," . $right->getLevel() . ",0)");
                                                }
                                                else {
                                                    $conn->query("insert into classrights (granteeid, granteetype, identityproviderid, classid, level, weight) values ('" . $right->getGranteeId() . "','user','" . $right->getIdentityProviderId() . "','" . $classid . "'," . $right->getLevel() . ",0)");
                                                }
                                            }
                                        }
                                        $conn->query("insert into events (sourceid, type, userid, identityproviderid) values ('" . $classid . "','class.added', '" . $this->userId . "','" . $this->identityProviderId . "')");
                                    }
                                    if (isset($definition->objectrelationtypes)) {
                                        foreach($definition->objectrelationtypes as $objectrelationtype) {
                                            $conn->query("insert into objectrelationtypes (name, storeid, object1type, object2type) values ('" . $objectrelationtype->name . "','" . $id . "','" . $objectrelationtype->object1type . "', '" . $objectrelationtype->object2type . "')");
                                        }
                                    }
                                }
                                else {
                                    $succeeded = false;
                                }
                            }
                        }
                    }
                    if ($succeeded) {
                        $conn->query("insert into events (sourceid, type, userid, identityproviderid) values ('" . $id . "','store.added', '" . $this->userId . "','" . $this->identityProviderId . "')");
                        $conn->commit();
                        return $id;
                    }
                    else {
                        $conn->rollback();
                        return false;
                    }
                }
                catch (Exception $err) {
                    $conn->rollback();
                }
            }
            finally {
                $conn->close();
            }
        }
        public function createClass($storeid, $class) {
            try {
                $succeeded = true;
                $errors = [];
                $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
                $conn->begin_transaction();

                $idquery = $conn->query("select UUID() newid from INFORMATION_SCHEMA.TABLES LIMIT 1");
                $id = $idquery->fetch_object()->newid;

                if (is_null($class->getParentClass()))
                    $succeeded = $conn->query("insert into classes (id, name, creator, creatoridentityproviderid, folderclass, contentclass, storeid) values ('" . $id . "','" . $class->getName() . "','" . $this->userId . "','" . $this->identityProviderId . "'," . ($class->isFolderClass() ? '1' : '0') . "," . ($class->isDocumentClass() ? '1': '0') . ",'" . $storeid . "')");
                else {
                    $potentialClasses = $conn->query("select id from classes where storeid = '" . $storeid . "' and (id = '" . $class->getParentClass() . "' or name = '" . $class->getParentClass() . "')");
                    if ($potentialClass = $potentialClasses->fetch_object()) {
                        $succeeded = $conn->query("insert into classes (id, name, creator, creatoridentityproviderid, folderclass, contentclass, storeid, parentclassid) values ('" . $id . "','" . $class->getName() . "','" . $this->userId . "','" . $this->identityProviderId . "'," . ($class->isFolderClass() ? '1' : '0') . "," . ($class->isDocumentClass() ? '1': '0') . ",'" . $storeid . "','" . $potentialClass->id . "')");
                    }
                }
                if ($succeeded) {
                    foreach($class->getProperties() as $property) {
                        if ($conn->query("insert into classproperties (id, name, `type`, required, multiple, classid) values (UUID(), '" . $property->getName() . "','" . $property->getType() . "'," . ($property->isRequired()? 1:0) . "," . ($property->isMultiple()? 1: 0). ",'" . $id . "')")) {
                            //Do nothing
                        }
                        else {
                            $succeeded = false;
                            $errors[sizeof($errors)] = "Property '" . $property->getName() . "' could not be added.";
                            break;
                        }

                    }
                    if ($succeeded)
                        Logger::log('Create class: Add rights ' . sizeof($class->getRights()));
                        if (sizeof($class->getRights()) == 0) {
                            $defaultRightsQuery = "insert into classrights (granteeid, granteetype, identityproviderid, classid, level, weight) values ('" . $this->userId . "','user','" . $this->identityProviderId . "','". $id . "',(select sum(level) from rights where classright = 1), 1)";
                            Logger::log('Create class: Default rights query: ' . $defaultRightsQuery);
                            $succeeded = $conn->query($defaultRightsQuery);
                        }
                        else {
                            foreach($class->getRights() as $right) {
                                if ($right->getGranteeType() == 'user')
                                    $succeeded = $conn->query("insert into classrights (granteeid, granteetype, identityproviderid, classid, level, weight) values ('" . $right->getGranteeId() . "','user','" . $right->getIdentityProviderId() . "','" . $id . "'," . $right->getLevel() . ",0)");
                                else if ($right->getGranteeType() == 'role')
                                    $succeeded = $conn->query("insert into classrights (granteeid, granteetype, identityproviderid, classid, level, weight) values ('" . $right->getGranteeId() . "','role','" . $right->getIdentityProviderId() . "','" . $id . "'," . $right->getLevel() . ",0)");
                                else if ($right->getGranteeType() == 'special')
                                    $succeeded = $conn->query("insert into classrights (granteeid, granteetype, classid, level, weight) values ('everyone', 'special', '" . $id . "'," . $right->getLevel() . ",0)");
                                else {
                                    $succeeded = false;
                                }
                                if ($succeeded) {
                                    //Do nothing
                                }
                                else {
                                    $succeeded = false;
                                    break;
                                }
                            }
                        }
                        if ($succeeded) {
                            $conn->query("insert into events (sourceid, type, userid, identityproviderid) values ('" . $id . "','class.added','" . $this->userId . "','" . $this->identityProviderId . "')");
                            $conn->commit();
                            return $id;
                        }
                        else { 
                            $conn->rollback();
                            $errors[sizeof($errors)] = "Rights could not be added";
                            return $errors;
                        }
                    }
                    else {
                        $conn->rollback();
                        $errors[sizeof($errors)] = "Class could not be added";
                        return $errors;
                    }
            }
            finally {
                $conn->close();
            }
        }

        public function areValidRights($rights) {
            try {
                $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
                $checked = [];
                $result = true;
                foreach($rights as $right) {
                    if ($right->grantee != 'everyone') {
                        $results = $conn->query("select id,name from identityproviders where id = '". $right->identityprovider . "' or name = '" . $right->identityprovider . "'");
                        if ($results->num_rows == 0) {
                            $result = false;
                            break;
                        }
                        else {
                            $idp = $results->fetch_object();
                            if ($idp->name == $right->identityprovider)
                                $right->identityprovider = $idp->id;
                        }
                    }
                }
                return $result;
            }
            finally {
                $conn->close();
            }
        }

        public function getClasses($storeid) {
            try {
                Logger::log('getClasses: userId: ' . $this->userId);
                $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
                $readRight = $conn->query("select level from rights where systemright='read'");
                if ($right = $readRight->fetch_object()) {
                    $classes = [];
                    Logger::log('getClasses: query: ' . "select id, name, folderclass, contentclass, parentclassid from classes where storeid = '" . $storeid . "' and hasClassRight(id, '" . $this->userId . "','" . $this->identityProviderId . "'," . $right->level . ")");
                    $rows = $conn->query("select id, name, folderclass, contentclass, parentclassid from classes where storeid = '" . $storeid . "' and hasClassRight(id, '" . $this->userId . "','" . $this->identityProviderId . "'," . $right->level . ")");
                    while ($row = $rows->fetch_object()) {
                        $newClass = new AdaClass($row->id, $row->name);
                        $newClass->setIsDocumentClass($row->contentclass == 1);
                        $newClass->setIsFolderClass($row->folderclass == 1);
                        if (!is_null($row->parentclassid))
                            $newClass->setParentClass($row->parentclassid);
                        $classes[sizeof($classes)] = $newClass;
                    }
                    return $classes;
                }
                else
                    return [];
            }
            finally {
                $conn->close();
            }
        }

        public function getClass($storeid, $classid) {
            try {
                $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
                $rows = $conn->query("select id, name, folderclass, contentclass, parentclassid from classes where storeid = '" . $storeid . "' and (id = '" . $classid . "' or name = '" . $classid . "')");
                if ($rows && $rows->num_rows > 0) {
                    $row = $rows->fetch_object();
                    $classid = $row->id;
                    $newClass = new AdaClass($row->id, $row->name);
                    $newClass->setIsFolderClass($row->folderclass == 1);
                    $newClass->setIsDocumentClass($row->contentclass == 1);
                    if (!is_null($row->parentclassid)) {
                        $newClass->setParentClass($row->parentclassid);
                    }

                    $currentId = $classid;
                    while (!is_null($currentId)) {
                        $props = $conn->query("select id, name, `type`, required, multiple from classproperties where classid = '" . $currentId . "'");
                        if ($props) {
                            while ($prop = $props->fetch_object()) {
                                $newProp = new Property($prop->id, $prop->name, $prop->type);
                                $newProp->setMultiple($prop->multiple == 1);
                                $newProp->setRequired($prop->required == 1);
                                $newClass->addProperty($newProp);
                            }
                        }
                        $parentClassRow = $conn->query("select parentclassid from classes where id = '" . $currentId . "'");
                        if ($parentClassRow && $parentClassRow->num_rows == 1) {
                            $parentRow = $parentClassRow->fetch_object();
                            $currentId = $parentRow->parentclassid;
                        }
                        else {
                            $currentId = NULL;
                        }
                    }

                    $rights = $conn->query("select r.id, r.granteeid, r.granteetype, r.identityproviderid, r.level, r.weight, idp.type, u.id userid, u.email, u.firstname, u.lastname from classrights r left outer join identityproviders idp on idp.id = r.identityproviderid left outer join users u on u.id = r.granteeid where r.classid = '" . $classid . "'");
                    if ($rights) {
                        while ($right = $rights->fetch_object()) {
                            $newRight = new GrantedRight($right->granteeid, $right->granteetype, $right->identityproviderid, $right->level, $right->weight);
                            if ($right->type == 'internal') {
                                $newRight->setUser(new User($right->userid, $right->email, $right->firstname, $right->lastname, $right->identityproviderid));
                            }
                            $newClass->addRight($newRight);
                        }
                    }

                    //Register get class event
                    $conn->query("insert into events (type,sourceid, userid, identityproviderid) values ('class.get', '" . $classid . "','" . $this->userId . "','" . $this->identityProviderId . "')");
                    return $newClass;
                }
                else
                    return false;
            }
            finally {
                $conn->close();
            }

        }

        public function getRights() {
            try {
                $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
                $rows = $conn->query("select id, name, systemright, level, domainright, storeright, classright, objectright from rights");
                $rights = [];
                while ($row = $rows->fetch_object()) {
                    $rights[sizeof($rights)] = new Right($row->id, $row->name, $row->systemright, $row->level, $row->domainright, $row->storeright, $row->classright, $row->objectright);
                }
                return $rights;
            }
            finally {
                $conn->close();
            }
        }

        public function deleteStore($storeid) {
            try {
                $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
                $contents = $conn->query("select c.id, c.localdir from content c inner join objects o on (o.id = c.objectid and o.storeid = '" . $storeid . "')");
                while ($content = $contents->fetch_object()) {
                    $file = $content->localdir. "/" . $content->id;
                    unlink($file);
                }
                return $conn->query("call deleteStore('" . $storeid . "','" . $this->userId . "','" . $this->identityProviderId . "')");
            }
            finally {
                $conn->close();
            }
        }

        public function deleteObject($storeid, $objectid) {
            try {
                $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
                $contents = $conn->query("select c.id, c.localdir from content c inner join objects o on (o.id = c.objectid and o.storeid = '" . $storeid . "' and o.objectid = '" . $objectid . "')");
                if ($contents) {
                    while ($content = $contents->fetch_object()) {
                        $file = $content->localdir. "/" . $content->id;
                        unlink($file);
                    }
                }
                return $conn->query("call deleteObject('" . $objectid . "','" . $this->userId . "','" . $this->identityProviderId . "')");
            }
            finally {
                $conn->close();
            }
        }

        public function canAddAddon() {
            try {
                $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
                $systemrights = $conn->query("select level from rights where systemright='createaddon'");
                if ($systemright = $systemrights->fetch_object()) {
                    $hasRights = $conn->query("select hasDomainRight('" . $this->userId . "','" . $this->identityProviderId . "'," . $systemright->level . ") hasright from rights LIMIT 1");
                    if ($hasRights->num_rows == 1) {
                        return $hasRights->fetch_object()->hasright == 1;
                    }
                    else {
                        return false;
                    }
                }
                else
                    return false;
            }
            finally {
                $conn->close();
            }
        }

        public function canGetAddons() {
            try {
                $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
                $systemrights = $conn->query("select level from rights where systemright='getaddons'");
                if ($systemright = $systemrights->fetch_object()) {
                    $hasRights = $conn->query("select hasDomainRight('" . $this->userId . "','" . $this->identityProviderId . "'," . $systemright->level . ") hasright from rights LIMIT 1");
                    if ($hasRights->num_rows == 1) {
                        return $hasRights->fetch_object()->hasright == 1;
                    }
                    else {
                        return false;
                    }
                }
                else
                    return false;
            }
            finally {
                $conn->close();
            }
        }

        public function getAddOns() {
            try {
                $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
                $results = $conn->query("select id, name from addons order by name");
                $json = "[";
                $prefix = "";
                while ($result = $results->fetch_object()) {
                    $json .= ($prefix . "{\"id\":\"" . $result->id . "\",\"name\":\"" . $result->name . "\"}");
                    $prefix = ",";
                }
                $json .= "]";
                return $json;
            }
            finally {
                $conn->close();
            }
        }

        public function canUpdateAddon() {
            try {
                $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
                $systemrights = $conn->query("select level from rights where systemright='updateaddon'");
                if ($systemright = $systemrights->fetch_object()) {
                    $hasRights = $conn->query("select hasDomainRight('" . $this->userId . "','" . $this->identityProviderId . "'," . $systemright->level . ") hasright from rights LIMIT 1");
                    if ($hasRights->num_rows == 1) {
                        return $hasRights->fetch_object()->hasright == 1;
                    }
                    else {
                        return false;
                    }
                }
                else
                    return false;
            }
            finally {
                $conn->close();
            }
        }

        public function addAddon($id, $name, $json) {
            try {
                $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
                $checks = $conn->query("select id from addons where id = '" . $id . "'");
                if ($check = $checks->fetch_object())
                    throw new Exception("Addon with id '" . $id . "' already exists");
                else {
                    return $conn->query("insert into addons (id, name, structure) values ('" . $id . "','" . $name . "','" . $conn->real_escape_string($json) . "')");
                }
            }
            finally {
                $conn->close();
            }

        }

        public function updateAddon($id, $name, $json) {
            try {
                $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
                $checks = $conn->query("select id from addons where id = '" . $id . "'");
                if ($check = $checks->fetch_object())
                    return $conn->query("update addons set name='". $name . "', structure = '" . $conn->real_escape_string($json) . "' where id = '" . $id . "'");
                else {
                    throw new Exception("Addon with id '" . $id . "' not exists");
                }
            }
            finally {
                $conn->close();
            }

        }

        public function canCreateObject($classid) {
            try {
                $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
                $rights = $conn->query("select level from rights where systemright='createobject'");
                if ($rights->num_rows == 1) {
                    $hasRights = $conn->query("select hasClassRight(id, '" . $this->userId . "','" . $this->identityProviderId . "'," . $rights->fetch_object()->level . ") hasright from classes where id = '" . $classid . "'");
                    if ($hasRights->num_rows == 1) {
                        return $hasRights->fetch_object()->hasright == 1;
                    }
                    else 
                        return false;
                }
                else 
                    return false;
            }
            finally {
                $conn->close();
            }
       }

       public function createObject($storeid, $class, $request) {
            try {
                $detailErrorMessage = 'Object creation failed';
                $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
                $idquery = $conn->query("select UUID() newid from INFORMATION_SCHEMA.TABLES LIMIT 1");
                $id = $idquery->fetch_object()->newid;
                $succeeded = true;
                $conn->begin_transaction();
                $succeeded = $conn->query("insert into objects (id, classid, storeid, creator, creatoridentityproviderid) values ('" . $id . "','" . $class->getId() . "','" . $storeid . "','" . $this->userId . "','" . $this->identityProviderId . "')");
                if ($succeeded) {
                    if (!isset($request->rights) || gettype($request->rights) == "NULL" or sizeof($request->rights) == 0) {
                        $succeeded = $conn->query("insert into objectrights (granteeid, granteetype, identityproviderid, objectid, level, weight) values ('" . $this->userId . "','user','" . $this->identityProviderId . "','". $id . "',(select sum(level) from rights where objectright = 1), 1)");
                    }
                    else {
                        foreach($request->rights as $right) {
                            if ($succeeded) {
                                if ($right->grantee == 'everyone') 
                                    $succeeded = $conn->query("insert into objectrights (granteeid, granteetype, objectid, level, weight) values ('everyone', 'user', '" . $id . "'," . $right->level . ",0)");
                                else {
                                    $succeeded = $conn->query("insert into objectrights (granteeid, granteetype, identityproviderid, objectid, level, weight) values ('" . $right->grantee . "','" . $right->granteetype . "','" . $right->identityprovider . "','" . $id . "'," . $right->level . ",0)");
                                }
                            }
                        }
                    }
                    if ($succeeded) {
                        if (isset($request->properties) && gettype($request->properties) == 'object') {
                            //Collect all properties
                            $currentId = $class->getId();
                            $propertyDefs = [];
                            while (!is_null($currentId)) {
                                $propNames = $conn->query("select name from classproperties where classid = '" . $currentId . "'");
                                if ($propNames) {
                                    while ($propDef = $propNames->fetch_object()) {
                                        $propertyDefs[$propDef->name] = $currentId;
                                    }
                                }
                                $parentRow = $conn->query("select parentclassid from classes where id = '" . $currentId . "'");
                                if ($parentRow) {
                                    $parent = $parentRow->fetch_object();
                                    $currentId = $parent->parentclassid;
                                }
                            }
                            $propertyNames = array_keys( get_object_vars($request->properties));
                            foreach($propertyNames as $property) {
                                if (array_key_exists($property, $propertyDefs)) {
                                    $propertyProps = $conn->query("select id, type from classproperties where classid = '" . $propertyDefs[$property] . "' and name ='" . $property . "'");
                                    if ( $propertyProps->num_rows == 1) {
                                        $props = $propertyProps->fetch_object();
                                        $type = $props->type;
                                        $propid = $props->id;
                                        if ($type == 'string') {
                                            $succeeded = $conn->query("insert into objectproperties (objectid,propertyid,string_value) values ('" . $id . "','" . $propid . "','" . $request->properties->$property . "')");
                                        }
                                        else if ($type == 'date') {
                                            $succeeded = $conn->query("insert into objectproperties (objectid,propertyid, date_value) values('" . $id . "','" . $propid . "','" . $request->properties->$property->year . '-' . $request->properties->$property->month . '-' . $request->properties->$property->day . "')");
                                        }
                                        else if ($type == 'object') {
                                            $checkObject = $conn->query("select count(*) count from objects where id = '" . $request->properties->$property . "' and storeid = '" . $storeid . "'");
                                            if ($checkObject->fetch_object()->count == 1) {
                                                $succeeded = $conn->query("insert into objectproperties (objectid,propertyid,string_value) values ('" . $id . "','" . $propid . "','" . $request->properties->$property . "')");
                                            }
                                            else {
                                                $succeeded = false;
                                                $detailErrorMessage = "Invalid valid for property '" . $property . "'";
                                            }
                                        }
                                        else {
                                            $succeeded = false;
                                            $detailErrorMessage = "Unsupported property type (" . $type . ")";
                                        }
                                    }
                                    else {
                                        $succeeded = false;
                                        $detailErrorMessage = "Unknown property (" . $property . ")";
                                    }
                                }
                                else {
                                    $succeeded = false;
                                    $detailErrorMessage = "Unknown property (" . $property . ")";
                                }
                                if (!$succeeded)
                                    break;
                            }
                        }
                        if ($succeeded) {
                            if (isset($request->content) && gettype($request->content) == "object" && $class->isDocumentClass()) {
                                $date = new DateTime();
                                $saveDir = $this->contentDir . "/" . $date->format("Y") . "/" . $date->format("m") . "/" . $date->format("d");
                                if (!is_dir($saveDir))
                                    mkdir($saveDir, 0777, true);
                                $contentidquery = $conn->query("select UUID() newid from INFORMATION_SCHEMA.TABLES LIMIT 1");
                                $contentId = $contentidquery->fetch_object()->newid;
                                $contentfile = fopen($saveDir . "/" . $contentId, "w");
                                fwrite($contentfile, base64_decode($request->content->content));
                                fclose($contentfile);
                                if ($request->content->minorversion) {
                                    $majorversion = 0;
                                    $minorversion = 1;
                                }
                                else {
                                    $majorversion = 1;
                                    $minorversion = 0;
                                }
                                $succeeded = $conn->query("insert into content (id, objectid, size, mimetype, majorversion, minorversion, localdir, uploadfile) values ('" . $contentId . "','" . $id . "'," . filesize($saveDir . "/" . $contentId) . ",'". $request->content->mimetype . "',". $majorversion . "," . $minorversion. ",'" . $saveDir . "','" . $request->content->uploadfile . "')");
                                if (!$succeeded)
                                    $detailErrorMessage = "Content kon niet worden toegevoegd";
                            }
                            $conn->query("insert into events (type,sourceid, userid, identityproviderid) values ('object.added', '" . $id . "','" . $this->userId . "','" . $this->identityProviderId . "')");
                            $conn->commit();
                            return new AdaObject($id, $class);
                        }
                        else {
                            $conn->rollback();
                            throw new Exception($detailErrorMessage);
                        }
                    }
                    else {
                        $conn->rollback();
                        throw new Exception($detailErrorMessage);
                    }
                }
                else {
                    $conn->rollback();
                    throw new Exception($detailErrorMessage);
                }
            }
            finally {
                $conn->close();
            }
       }

       public function getObjectClassId($objectid) {
        try {
            $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
            $idquery = $conn->query("select classid from objects where id = '" . $objectid . "'");
            if ($idquery) {
                return $idquery->fetch_object()->classid;
            }
            else
                return NULL;
        }
        finally {
            $conn->close();
        }
       }

       public function getObject($storeidorname, $objectid) {
        try {
            $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
            $storesearchresult = $conn->query("select id from stores where id = '" . $storeidorname . "' or name = '" . $storeidorname . "'");
            if ($storesearchresult) {
                $store = $storesearchresult->fetch_object();
                $storeid = $store->id;
                $rows = $conn->query("select o.id, o.classid, c.majorversion, c.minorversion, c.mimetype, co.userid, co.identityproviderid from objects o left outer join content c on c.objectid = o.id left outer join checkouts co on co.objectid = o.id where o.storeid = '" . $storeid . "' and o.id = '" . $objectid . "' order by c.majorversion desc, c.minorversion desc limit 1");
                if ($rows) {
                    $row = $rows->fetch_object();
                    $newObject = new AdaObject($row->id, $row->classid);
                    $propRows = $conn->query("select pd.id, pd.name, pd.type, p.string_value, p.date_value from objectproperties p inner join classproperties pd on p.propertyid = pd.id where p.objectid = '" . $objectid . "'");
                    while ($propRow = $propRows->fetch_object()) {
                        if ($propRow->type == 'string')
                            $newObject->addProperty($propRow->id, $propRow->name, $propRow->type, $propRow->string_value);
                        else {
                            $dateItems = explode('-', $propRow->date_value);
                            $newObject->addProperty($propRow->id, $propRow->name, $propRow->type, ["day" => intval($dateItems[2]), "month"=>intval($dateItems[1]), "year"=>intval($dateItems[0])]);
                        }
                    }
                    if (!is_null($row->majorversion)) {
                        if (is_null($row->userid))
                            $newObject->setContent($row->majorversion, $row->minorversion, $row->mimetype, false);
                        else
                            $newObject->setContent($row->majorversion, $row->minorversion, $row->mimetype, true, $row->userid, $row->identityproviderid);
                    }

                    //Rights
                    $rights = $conn->query("select r.id, r.granteeid, r.granteetype, r.identityproviderid, r.level, r.weight, idp.type, u.id userid, u.email, u.firstname, u.lastname from objectrights r left outer join identityproviders idp on idp.id = r.identityproviderid left outer join users u on u.id = r.granteeid where r.objectid = '" . $newObject->getId() . "'");
                    if ($rights) {
                        while ($grantedright = $rights->fetch_object()) {
                            $newRight = new GrantedRight($grantedright->granteeid, $grantedright->granteetype, $grantedright->identityproviderid, $grantedright->level, $grantedright->weight);
                            if ($grantedright->type == 'internal') {
                                $newRight->setUser(new User($grantedright->userid, $grantedright->email, $grantedright->firstname, $grantedright->lastname, $grantedright->identityproviderid));
                            }
                            $newObject->addRight($newRight);
                        }

                        //Register get object event
                        $conn->query("insert into events (type,sourceid, userid, identityproviderid) values ('object.get', '" . $objectid . "','" . $this->userId . "','" . $this->identityProviderId . "')");

                        return $newObject;
                    }
                    else
                        return false;
                }
                else
                    return false;
            }
            else 
                return false;
        }
        finally {
            $conn->close();
        }
    }

    public function getObjectPath($storeidorname, $objectid) {
        try {
            $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
            $storesearchresult = $conn->query("select id from stores where id = '" . $storeidorname . "' or name = '" . $storeidorname . "'");
            if ($storesearchresult) {
                $store = $storesearchresult->fetch_object();
                $storeid = $store->id;
                $currentId = $objectid;
                $readRight = $conn->query("select level from rights where systemright='read'");
                if ($readRight) {
                    $right = $readRight->fetch_object();
                    $readlevel = $right->level;
                    $pathEls = [];
                    $pathCompleted = false;
                    $pathCancelled = false;
                    while (!$pathCompleted && !$pathCancelled) {
                        $rows = $conn->query("select o.id, o.classid, r.level, c.folderclass from objects o inner join objectrights r on o.id = r.objectid left outer join classes c on c.id = o.classid where o.storeid = '" . $storeid . "' and o.id = '" . $currentId . "' and (r.granteeid = 'everyone' or (r.granteeid = '" . $this->userId . "' and r.identityproviderid = '" . $this->identityProviderId . "')) order by r.weight asc limit 1");
                        if ($rows) {
                            $row = $rows->fetch_object();
                            if (intval($row->level) & intval($readlevel)) {
                                if ($row->folderclass) {
                                    $namerow = $conn->query("select op.string_value from objectproperties op inner join classproperties cp on cp.classid = '" . $row->classid . "' and op.objectid = '" . $currentId . "' and cp.name = 'Name' and op.propertyid = cp.id");
                                    if ($namerow) {
                                        $pathEls[sizeof($pathEls)] = ["id" => $currentId, "name" => $namerow->fetch_object()->string_value];
                                    }
                                    $parentRow = $conn->query("select op.string_value from objectproperties op inner join classproperties cp on cp.classid = '" . $row->classid . "' and op.objectid = '" . $currentId . "' and op.propertyid = cp.id and cp.name = 'ParentFolder'");
                                    if ($parentRow) {
                                        $currentId = $parentRow->fetch_object()->string_value;
                                        if (strlen($currentId) == 0)
                                            $pathCompleted = true;
                                    }
                                    else
                                        $pathCompleted = true;
                                }
                                else 
                                    $pathCancelled = true;
                            }
                            else
                                $pathCancelled = true;
                        }
                        else
                            $pathCancelled = true;
                    }
                    if ($pathCompleted) {
                        return $pathEls;
                    }
                    else {
                        return false;
                    }
                }
                else
                    return [2];
            }
            else
                return [3];
        }
        finally {
            $conn->close();
        }
    }

    public function search($storeidorname, $search) {
        try {
            $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
            $storesearchresult = $conn->query("select id from stores where id = '" . $storeidorname . "' or name = '" . $storeidorname . "'");
            if ($storesearchresult) {
                $storeid = $storesearchresult->fetch_object()->id;
                $readRight = $conn->query("select level from rights where systemright='read'");
                if ($readRight) {
                    if ($right = $readRight->fetch_object()) {
                        $class = $this->getClass($storeid, $search->class);
                        $query = "select o.id, o.classid";
                        $from = "";
                        if (isset($search->properties)) {
                            $propertyIndex = 1;
                            foreach($search->properties as $property) {
                                foreach($class->getProperties() as $classproperty) {
                                    if ($classproperty->getName() == $property) {
                                        $query .= ",op" . $propertyIndex . ".";
                                        if ($classproperty->getType() == 'string')
                                            $query .= "string_value";
                                        else if ($classproperty->getType() == 'date')
                                            $query .= "date_value";
                                        $query .= " PropertyValue" . $propertyIndex;
                                        $query .= ", cp" . $propertyIndex . ".id PropertyId" . $propertyIndex;
                                        $query .= ", cp" . $propertyIndex . ".type PropertyType" . $propertyIndex;
                                        $from .= " inner join objectproperties op". $propertyIndex . " on o.id = op" . $propertyIndex . ".objectid inner join classproperties cp" . $propertyIndex . " on op" . $propertyIndex . ".propertyid = cp" . $propertyIndex . ".id and cp" . $propertyIndex . ".name = '" . $property . "'";
                                        $propertyIndex++;
                                    }
                                }
                            }
                        }
                        $query .= " from objects o" . $from;
                        $filterindex = 1;
                        $classIds = $this->getChildClasses($conn, $class->getId());

                        $where = "o.storeid = '" . $storeid . "' and o.classid in (";
                        $classidprefix = "";
                        foreach( $classIds as $classId) {
                            $where = $where . $classidprefix . "'". $classId . "'";
                            $classidprefix = ',';
                        }
                        $where .= ") and hasObjectRight(o.id, '" . $this->userId . "','" . $this->identityProviderId . "'," . $right->level . ")";
                        $errors = [];
                        foreach($search->filters as $filter) {
                            $property = $filter->property;
                            $propertyId = null;
                            $propertyType = null;
                            foreach($class->getProperties() as $classproperty) {
                                if ($classproperty->getName() == $property) {
                                    $propertyId = $classproperty->getId();
                                    $propertyType = $classproperty->getType();
                                    break;
                                }
                            };
                            $operator = $filter->operator;

                            if ($operator == 'isnull') {
                                $query .= " left outer join objectproperties f" . $filterindex . " on (o.id = f" . $filterindex . ".objectid and f" . $filterindex . ".propertyid = '" . $propertyId . "')";
                                $where .= " and " . "f" . $filterindex . ".objectid is null";
                                $filterindex++;
                            }
                            else if ($operator == 'equals') {
                                $query .= " left outer join objectproperties f" . $filterindex . " on (o.id = f" . $filterindex . ".objectid and f". $filterindex . ".propertyid = '" . $propertyId . "')";
                                if ($propertyType == 'object') {
                                    if (isset($filter->value) && gettype($filter->value) == 'string') {
                                        $where .= " and f" . $filterindex . ".string_value = '" . $filter->value . "'";
                                    }
                                    else {
                                        $errors[sizeof($errors)] = "Invalid filter value for property '" . $property . "' (" . $filter->value . ")";
                                    }
                                }
                                else if ($propertyType == 'string') {
                                    if (gettype($filter->value) == 'string') {
                                        $where .= " and f" . $filterindex . ".string_value = '" . $filter->value . "'";
                                    }
                                    else {
                                        $errors[sizeof($errors)] = "Invalid filter value for property '" . $property . "' (" . $filter->value . ")";
                                    }
                                }
                            }
                        }
                        $query .= " where " . $where . " order by o.id";
                        Logger::log("Search: " . $query);
                    }
                    else {
                        $errors[sizeof($error)] = 'Unknown right';
                    }
                }
                else {
                    $errors[sizeof($errors)] = 'Unknown right';
                }

                if (sizeof($errors) == 0) {
                    $lastObjectId = "";
                    $foundObjects = [];
                    try {
                        $objectresults = $conn->query($query);
                        while ($row = $objectresults->fetch_object()) {
                            $objectId = $row->id;
                            if ($objectId != $lastObjectId) {
                                $newResult = new AdaObject($objectId, $row->classid);
                                $propertyIndex = 1;
                                foreach($search->properties as $property) {
                                    $propertyId = 'PropertyId' . $propertyIndex;
                                    $propertyValue = 'PropertyValue' . $propertyIndex;
                                    $propertyType = 'PropertyType' . $propertyIndex;
                                    $newResult->addProperty($row->$propertyId, $property, $row->$propertyType, $row->$propertyValue);
                                    $propertyIndex++;
                                }
                                $foundObjects[sizeof($foundObjects)] = $newResult;
                                $lastObjectId = $objectId;
                            }
                        }
                        $json = "[";
                        $prefix = "";
                        foreach($foundObjects as $object) {
                            $json .= $prefix;
                            if (isset($search->tree)) {
                                $jsonObject = json_decode($object->toJson());
                                $childrenSearch = "{\"class\":\"" . $search->class . "\"";
                                $childrenSearch .= ",\"filters\":[";
                                $filterPrefix = ""; 
                                foreach($search->treefilters as $treefilter) {
                                    $childrenSearch .= $filterPrefix;
                                    $childrenSearch .= "{";
                                    $childrenSearch .= "\"property\":\"" . $treefilter->property . "\",";
                                    $childrenSearch .= "\"operator\":\"" . $treefilter->operator . "\",";
                                    $childrenSearch .= "\"value\":\"" . str_replace("{treeitem}.id", $object->getId(), $treefilter->value) . "\"";
                                    $childrenSearch .= "}";
                                    $filterPrefix = ",";
                                }
                                $childrenSearch .= "],";
                                $childrenSearch .= "\"properties\":[";
                                $propertyPrefix = "";
                                foreach($search->properties as $treeproperty) {
                                    $childrenSearch .= $propertyPrefix . "\"". $treeproperty . "\"";
                                    $propertyPrefix = ",";
                                }
                                $childrenSearch .= "]";
                                $childrenSearch .= ",\"tree\": true,";
                                $childrenSearch .= "\"treefilters\":". json_encode($search->treefilters);
                                $childrenSearch .= "}";
                                $jsonObject->children = json_decode($this->search($storeidorname, json_decode($childrenSearch)));
                                $json .= json_encode($jsonObject);
                            }
                            else
                                $json .= $object->toJson();
                            $prefix = ",";
                        }
                        $json .= "]";
                        return $json;
                    }
                    finally {
                        $conn->close();
                    }
                }
                else
                    return $errors;
            }
            else {
                $errors = [];
                $errors[0] = 'Store not found (' . $storeidorname . ")";
                return $errors;
            }
        }
        finally {}
    }

    private function getChildClasses($conn, $classid) {
        $ids = [];
        $ids[0] = $classid;
        $classids = $conn->query("select id from classes where parentclassid = '" . $classid . "'");
        while ($row = $classids->fetch_object()) {
            $childids = $this->getChildClasses($conn, $row->id);
            foreach($childids as $childid) {
                $ids[sizeof($ids)] = $childid;
            }
        }
        return $ids;
    }

    public function canGetContent($storeid, $objectid) {
        try {
            $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
            $readRight = $conn->query("select level from rights where systemright='getcontent'");
            if ($readRight && $readRight->num_rows == 1) {
                $right = $readRight->fetch_object();
                $rows = $conn->query("select id, hasObjectRight(id, '" . $this->userId . ",'" . $this->identityProviderId . "'," . $right->level . ") hasright from objects where id = '" . $objectid . "' and storeid = '" . $storeid . "'");
                if ($rows && $rows->num_rows == 1) {
                    return $rows->fetch_object()->hasright == 1;
                }
                else
                    return false;
            }
            else
                return false;
       }
       finally {
           $conn->close();
       }
    }

    public function getContent($storeid, $objectid, $version) {
        try {
            $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
            if ($version == 'current') {
                $versionresults = $conn->query("select majorversion, minorversion from content where objectid = '" . $objectid . "' order by majorversion desc, minorversion desc limit 1");
                if ($currentversion = $versionresults->fetch_object()) {
                    $versions = [$currentversion->majorversion, $currentversion->minorversion];
                }
                else
                    return false;
            }
            else {
                $versions = explode(".", $version);
            }
            $rows = $conn->query("select id, mimetype, localdir, uploadfile, size from content where objectid = '" . $objectid . "' and majorversion = " . $versions[0] . " and minorversion = " . $versions[1]);
            if ($rows) {
                if ($row = $rows->fetch_object()) {
                    return new Content($objectid, $row->localdir . "/" . $row->id, $versions[0], $versions[1], $row->uploadfile, $row->mimetype, $row->size);
                }
                else 
                    return false;
            }
            else
                return false;
       }
       finally {
           $conn->close();
       }
    }

    public function canCheckout($storeid, $objectid) {
        try {
            $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);

            $readRight = $conn->query("select level from rights where systemright='checkout'");
            if ($readRight) {
                if ($right = $readRight->fetch_object()) {
                    $rows = $conn->query("select id, hasObjectRight(id, '" . $this->userId . "','" . $this->identityProviderId . "', " . $right->level . ") hasright from objects where id = '" . $objectid . "'");
                    if ($rows && $rows->num_rows == 1) {
                        return $rows->fetch_object()->hasright == 1;
                    }
                    else
                        return false;
                }
                else
                    return false;
            }
            else {
                return false;
            }
       }
       finally {
           $conn->close();
       }
    }

    public function checkout($storeid, $objectid) {
        try {
            $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
            $checkres = $conn->query("select userid, identityproviderid from checkouts where objectid = '" . $objectid . "'");
            if ($checkres->fetch_object()) {
                if ($checkres->userid == $this->userId && $checkres->identityproviderid == $this->identityProviderId) {
                    return true;
                }
                else
                    return false;
            }
            else {
                return $conn->query("insert into checkouts (objectid,userid,identityproviderid) values ('" . $objectid . "','" . $this->userId . "','" . $this->identityProviderId . "')");
            }
       }
       finally {
           $conn->close();
       }
    }

    public function canCheckin($storeid, $objectid) {
        try {
            $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
            $checkedout = $conn->query("select userid, identityproviderid from checkouts where objectid = '" . $objectid . "'");
            if ($checkoutrow = $checkedout->fetch_object()) {
                if ($checkoutrow->userid == $this->userId && $checkoutrow->identityproviderid == $this->identityProviderId) {
                    return true;
                }
                else {
                    $readRight = $conn->query("select level from rights where systemright = 'checkin'");
                    if ($readRight) {
                        if ($right = $readRight->fetch_object()) {
                            $rows = $conn->query("select id, hasObjectRight(id, '" . $this->userId . "','" . $this->identityProviderId . "'," . $right->level . ") hasright from objects where id = '" . $objectid . "'");
                            if ($rows && $rows->num_rows == 1) {
                                return $rows->fetch_object()->hasright == 1;
                            }
                            else
                                return false;
                        }
                        else 
                            return false;
                    }
                    else 
                        return false;
                }
            }
            else
                return false; //Not checked out
       }
       finally {
           $conn->close();
       }
    }

    public function checkin($storeid, $objectid, $content) {
        try {
            $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
            $versionres = $conn->query("select majorversion, minorversion from content where objectid = '". $objectid . "' order by majorversion desc, minorversion desc");
            if ($versionrow = $versionres->fetch_object()) {
                $majorversion = $versionrow->majorversion;
                $minorversion = $versionrow->minorversion;
            }
            $checkres = $conn->query("select userid, identityproviderid from checkouts where objectid = '" . $objectid . "'");

            //Add content
            $date = new DateTime();
            $saveDir = $this->contentDir . "/" . $date->format("Y") . "/" . $date->format("m") . "/" . $date->format("d");
            if (!is_dir($saveDir))
                mkdir($saveDir, 0777, true);
            $contentidquery = $conn->query("select UUID() newid from INFORMATION_SCHEMA.TABLES LIMIT 1");
            $contentId = $contentidquery->fetch_object()->newid;
            $contentfile = fopen($saveDir . "/" . $contentId, "w");
            fwrite($contentfile, base64_decode($content->content));
            fclose($contentfile);
            if ($content->minorversion) {
                $minorversion++;
            }
            else {
                $majorversion++;
                $minorversion = 0;
            }
            return $conn->query("insert into content (id, objectid, size, mimetype, majorversion, minorversion, localdir, uploadfile) values ('" . $contentId . "','" . $objectid . "'," . filesize($saveDir . "/" . $contentId) . ",'". $content->mimetype . "',". $majorversion . "," . $minorversion. ",'" . $saveDir . "','" . $content->uploadfile . "')");
        }
       finally {
           $conn->close();
       }
    }

    public function canEditClass($storeid, $classid) {
        try {
            $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
            $rights = $conn->query("select level from rights where systemright='Update'");
            if ($rights->num_rows == 1) {
                $results = $conn->query("select hasClassRight(id,'" . $this->userId . "','" . $this->identityProviderId . "'," . $rights->fetch_object()->level . ") hasright from classes where id = '" . $classid . "'");
                if ($results->num_rows == 1)
                    return $results->fetch_object()->hasright == 1;
                else
                    return false;
            }
            else
                return false;
        }
        finally {
            $conn->close();
        }
   }

   public function editProperty($classid, $propertyid, $request) {
    try {
        $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
        if (isset($request->name)) {
            return ($conn->query("update classproperties set name='" . $request->name . "' where id = '" . $propertyid . "'"));
        }
        else
            return false;
    }
    finally {
        $conn->close();
    }

   }

   public function addProperty($classid, $request) {
    try {
        $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
        $idquery = $conn->query("select UUID() newid from INFORMATION_SCHEMA.TABLES LIMIT 1");
        $id = $idquery->fetch_object()->newid;
        if ($conn->query("insert into classproperties (name, `type`, required, multiple, classid) values ('" . $request->name . "','" . $request->type . "'," . ($request->required ? 1 : 0) . "," . ($request->multiple? 1 : 0) . ",'" . $classid . "')")) {
            $conn->query("insert into events (sourceid, type, userid, identityproviderid) values ('" . $classid . "','class.changed','" . $this->userId . "','" . $this->identityProviderId . "')");
            return "{\"id\":\"" . $id . "\"}";
        }
        else
            return false;
    }
    finally {
        $conn->close();
    }
   }

   public function deleteProperty($propertyid) {
    try {
        $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
        return $conn->query("delete from classproperties where id = '" . $propertyid . "'");
    }
    finally {
        $conn->close();
    }
   }

   public function searchInternalUsers($search) {
    try {
        $conn= mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
        if ($idps = $conn->query("select id from identityproviders where type = 'internal'")) {
            if ($idp = $idps->fetch_object()) {
                $results = [];
                if ($users = $conn->query("select id, email, firstname, lastname from users where lastname like '%" . $search . "%'")) {
                    while ($user = $users->fetch_object()) {
                        $results[sizeof($results)] = new User($user->id, $user->email, $user->firstname, $user->lastname, $idp->id);
                    }
                    return $results;
                }
                else
                    return false;
            }
            else
                return false;
        }
        else
            return false;
    }
    finally {
        $conn->close();
    }
   }

   private function getGranteeTypeWeigth($type) {
    $weight = 0;
    if ($type == 'user')
        $weight = 10;
    else if ($type == 'role')
        $weight = 5;
    else if ($type == 'group')
        $weight = 3;
    return $weight;
   }

   public function updateClass($id, $request) {
    try {
        $classId = $id;
        $conn= mysqli_connect($this->host, $this->user, $this->password, $this->dbname);

        $conn->begin_transaction();
        $succeeded = true;

        //Update rights
        if ($conn->query("delete from classrights where classid = '" . $id . "'")) {
            foreach($request->rights as $right) {
                //$existing = $conn->query("select id, level from classrights where granteeid = '" . $right->grantee . "' and identityproviderid = '" . $right->identityprovider . "'");
                //if ($existing->num_rows == 0) {
                    $conn->query("insert into classrights (classid, granteeid, granteetype, identityproviderid, level, weight) values ('" . $id . "','" . $right->grantee . "','" . $right->granteetype . "','" . $right->identityprovider . "'," . $right->level . "," . $this->getGranteeTypeWeigth($right->granteetype) . ")");
                //}
                //else {
                //    $savedRight = $existing->fetch_object();
                //    $savedLevel = intval($savedRight->level);
                //    $requestedLevel = intval($right->level);
                //    if (($savedLevel & $requestedLevel) == 0)
                //        $conn->query("update classrights set level = " . ($savedRight + $requestedLevel) . " where id = '" . $savedRight->id . "'");
                //}
            }
        }
        else {
            $succeeded = false;
        }

        //Update properties
        foreach ($request->properties as $property) {
            if (isset($property->id)) {
                if ($conn->query("update classproperties set name='" . $property->name . "' where id = '" . $property->id . "'")) {
                    // 
                }
                else
                    $succeeded = false;
            }
            else {
                $classid = $id;
                $idquery = $conn->query("select UUID() newid from INFORMATION_SCHEMA.TABLES LIMIT 1");
                $id = $idquery->fetch_object()->newid;
                if ($conn->query("insert into classproperties (name, `type`, required, multiple, classid) values ('" . $property->name . "','" . $property->type . "'," . ($property->required ? 1 : 0) . "," . ($property->multiple? 1 : 0) . ",'" . $classid . "')")) {
                    //
                }
                else {
                    $succeeded = false;
                }
            }
        }

        if ($succeeded) {
            $conn->query("insert into events (sourceid,type,userid, identityproviderid) values ('" . $classId . "','class.changed','" . $this->userId . "','" . $this->identityProviderId . "')");
            $conn->commit();
            return true;
        }
        else {
            $conn->rollback();
            return false;
        }
    }
    catch (Exception $err) {
        $conn->rollback();
        return false;
    }
    finally {
        $conn->close();
    }
   }

   public function canEditObject($objectid) {
    try {
        $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
        $rights = $conn->query("select level from rights where systemright='Update'");
        if ($rights->num_rows == 1) {
            $checks = $conn->query("select hasObjectRight('" . $objectid . "','" . $this->userId . "','" . $this->identityProviderId . "'," . $rights->fetch_object()->level . ") hasright from objects where id = '" . $objectid . "'");
            if ($checks && $checks->num_rows == 1) {
                return $checks->fetch_object()->hasright == 1;
            }
            else {
                return false;
            }
        }
        else
            return false;
    }
    finally {
        $conn->close();
    }
   }

   public function updateObject($id, $request) {
    try {
        $conn= mysqli_connect($this->host, $this->user, $this->password, $this->dbname);

        $conn->begin_transaction();
        $succeeded = true;

        //Update rights
        if ($conn->query("delete from objectrights where objectid = '" . $id . "'")) {
            foreach($request->rights as $right) {
                if ($conn->query("insert into objectrights (objectid, granteeid, granteetype, identityproviderid, level, weight) values ('" . $id . "','" . $right->grantee . "','" . $right->granteetype . "','" . $right->identityprovider . "'," . $right->level . "," . $this->getGranteeTypeWeigth($right->granteetype) . ")")) {
                    //
                }
                else {
                    $succeeded = FALSE;
                }
            }
        }
        else {
            $succeeded = false;
        }

        //Update properties
        $foundPropIds = [];
        $foundPropNames = [];
        $existingproperties = $conn->query("select op.propertyid, cp.name from objectproperties op inner join classproperties cp on op.propertyid = cp.id where op.objectid = '" . $id . "'");
        while ($prop = $existingproperties->fetch_object()) {
            $foundPropIds[sizeof($foundPropIds)] = $prop->propertyid;
            $foundPropNames[sizeof($foundPropNames)] = $prop->name;
        }
        $handledPropertyIds = [];
        foreach ($request->properties as $property) {
            if (isset($property->id)) {
                $propertyId = $property->id;
            }
            else {
                if (array_search($property->name, $foundPropNames)) {
                    $propertyId = $foundPropIds[array_search($property->name, $foundPropNames)];
                }
                else {
                    $notexistingProperty = $conn->query("select id from classproperties where classid='" . $request->classid . "' and name = '" . $property->name . "'");
                    if ($nep = $notexistingProperty->fetch_object()) {
                        $propertyId = $nep->id;
                    }
                }
            }
            if (in_array($propertyId, $foundPropIds)) {
                if ($property->type == 'string' || $property->type == 'object') {
                    if ($conn->query("update objectproperties set string_value = '" . $conn->real_escape_string($property->value) . "' where objectid = '" . $id . "' and propertyid = '" . $propertyId . "'")) {
                        //
                    }
                    else {
                        $succeeded = FALSE;
                    }
                }
                else if ($property->type == 'date') {
                    if ($conn->query("update objectproperties set date_value = date('" . $property->value->year . '-' . $property->value->month . '-' . $property->value->day . "') where objectid = '" . $id . "' and propertyid = '" . $propertyId . "'")) {
                        //
                    }
                    else {
                        $succeeded = FALSE;
                    }
                }
            }
            else {
                if ($property->type == 'string' || $property->type == 'object') {
                    if ($conn->query("insert into objectproperties (objectid, propertyid, string_value) values ('" . $id . "','" . $propertyId . "','" . $conn->real_escape_string($property->value) . "')")) {
                        Logger::log("Property added");
                    }
                    else {
                        $succeeded = FALSE;
                    }
                }
                else if ($property->type == 'date') {
                    if ($conn->query("insert into objectproperties (objectid, propertyid, date_value) values ('" . $id . "','" . $propertyId . "',date('" . $property->value->year . '-' . $property->value->month . '-' . $property->value->day . "')")) {
                        //
                    }
                    else {
                        $succeeded = FALSE;
                    }
                }
            }
            $handledPropertyIds[sizeof($handledPropertyIds)] = $propertyId;
        }
        $deleteQuery = "delete from objectproperties where objectid = '" . $id . "' and propertyid not in (";
        $first = TRUE;
        foreach($handledPropertyIds as $handledId) {
            $deleteQuery .= ($first ? "" : ",") . "'" . $handledId . "'";
            $first = FALSE;
        }
        $deleteQuery .= ")";
        Logger::log("Update object: " . $deleteQuery);
        if ($conn->query($deleteQuery)) {
            //
        }
        else {
            $succeeded = FALSE;
        }

        if ($succeeded) {
            $conn->commit();
            return true;
        }
        else {
            $conn->rollback();
            return false;
        }
    }
    catch (Exception $err) {
        $conn->rollback();
        return false;
    }
    finally {
        $conn->close();
    }
   }

   public function canGetClass($storeid, $classid) {
    try {
        $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
        $readRight = $conn->query("select level from rights where systemright='read'");
        if ($readRight) {
            if ($right = $readRight->fetch_object()) {
                $rows = $conn->query("select id from classes where storeid = '" . $storeid . "' and (id = '" . $classid . "' or name = '" . $classid . "') and hasClassRight(id, '" . $this->userId . "','" . $this->identityProviderId . "'," . $right->level . ")");
                if ($rows) {
                    return ($rows && $rows->num_rows== 1);
                }
                else
                    return false;
            }
            else {
                return FALSE;
            }
        }
        else {
            return FALSE;
        }
    }
    finally {
        $conn->close();
    }

   }

   public function canGetObject($storeid, $objectid) {
    try {
        $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
        $storerows = $conn->query("select id from stores where id = '" . $storeid . "' or name = '" . $storeid . "'");
        if ($storerows) {
            $storeid = $storerows->fetch_object()->id;
            $countrow = $conn->query("select o.id, hasObjectRight(o.id, '" . $this->userId . "', '" . $this->identityProviderId . "', r.level) hasObjectRight from objects o inner join rights r on r.systemright = 'read' where o.storeid = '" . $storeid . "' and o.id = '" . $objectid . "'");
            if ($countrow->num_rows == 1) {
                $count = $countrow->fetch_object();
                return $count->hasObjectRight == 1;
            }
            else {
                return false;
            }
        }
        else
            return false;
    }
    finally {
        $conn->close();
    }
   }

   public function relateObjects($storeid, $id1, $id2, $type) {
    try {
        $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
        $object1 = $conn->query("select o.id,o.classid,c.folderclass,c.contentclass from objects o inner join classes c on c.id = o.classid where o.storeid = '" . $storeid . "' and o.id = '" . $id1 . "'");
        if ($object1 && $object1->num_rows == 1) {
            $object1data = $object1->fetch_object();
            $object2 = $conn->query("select o.id,o.classid,c.folderclass,c.contentclass from objects o inner join classes c on c.id = o.classid where o.storeid = '" . $storeid . "' and o.id = '" . $id2 . "'");
            if ($object2 && $object2->num_rows == 1) {
                $object2data = $object2->fetch_object();
                $types = $conn->query("select id, object1type, object2type from objectrelationtypes where storeid = '" . $storeid . "' and name = '" . $type . "'");
                if ($types && $types->num_rows == 1) {
                    $typeObject = $types->fetch_object();
                    $object1Ok = TRUE;
                    if ($typeObject->object1type == 'document')
                        $object1Ok = ($object1data->contentclass == 1);
                    else if ($typeObject->object1type == 'folder')
                        $object1Ok = ($object1data->folderclass == 1);
                    $object2Ok = TRUE;
                    if ($typeObject->object2type == 'document')
                        $object2Ok = ($object2data->contentclass == 1);
                    else if ($typeObject->object2type == 'folder')
                        $object2Ok = ($object2data->folderclass == 1);
                    if ($object1Ok && $object2Ok) {
                        $inserts = $conn->query("insert into objectrelations (object1, object2, type) values ('" . $id1 . "','" . $id2 . "','" . $typeObject->id . "')");
                        return true;
                    }
                    else {
                        return false;
                    }
                }
                else {
                    return false;
                }
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }
    finally {
        $conn->close();
    }
   }

   public function getRelatedObjects($objectid, $request) {
    try {
        $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
        $readRights = $conn->query("select level from rights where systemright = 'read'");
        if ($readRight = $readRights->fetch_object()) {
            if (isset($request->relationtype)) {
                $rows = $conn->query("select ors.object2 from objectrelations ors inner join objectrelationtypes rt on rt.id = ors.type where (rt.id = '" . $request->relationtype . "' or rt.name = '" . $request->relationtype . "') and ors.object1 = '" . $objectid . "'and hasObjectRight(ors.object2, '" . $this->userId . "','" . $this->identityProviderId . "', " . $readRight->level . ")");
            }
            else {
                $rows = $conn->query("select object2 from objectrelations where object1 = '" . $objectid . "'and hasObjectRight(ors.object2, '" . $this->userId . "','" . $this->identityProviderId . "', " . $readRight->level . ")");
            }
            $allowedObjectIds = [];
            while ($row = $rows->fetch_object()) {
                $allowedObjectIds[sizeof($allowedObjectIds)] = $row->object2;
            }
            $json = "[";
            $prefix = "";
            foreach($allowedObjectIds as $objectId) {
                $json .= $prefix . "{\"id\":\"" . $objectId . "\",";
                $classdetails = $conn->query("select c.id, c.name, c.contentclass from classes c inner join objects o on o.classid = c.id where o.id = '" . $objectId . "'");
                $classobject = $classdetails->fetch_object();
                $json .= "\"class\":{\"id\":\"". $classobject->id . "\",\"name\":\"" . $classobject->name . "\"},\"properties\":[";
                $propertiesQuery = "select op.propertyid, op.string_value, op.date_value, cp.type, cp.name from objectproperties op inner join classproperties cp on cp.id = op.propertyid where op.objectid = '" . $objectId . "' and cp.name in (";
                $propertyPrefix = "";
                foreach ($request->properties as $property) {
                    $propertiesQuery .= $propertyPrefix . "'" . $property . "'";
                    $propertyPrefix = ",";
                }
                $propertiesQuery .= ")";
                $firstProperty = true;
                $properties = $conn->query($propertiesQuery);
                while ($property = $properties->fetch_object()) {
                    $json .= ($firstProperty ? "" : ",");
                    $json .= "{\"type\":\"" . $property->type . "\",\"id\":\"" . $property->propertyid . "\",\"name\":\"" . $property->name . "\",\"value\":";
                    if ($property->type == 'string')
                        $json .= '"'. $property->string_value . '"';
                    else
                        $json .= "null";
                    $json .= "}";
                    $firstProperty = false;
                }
                $json .= "]";
                if ($classobject->contentclass) {
                    $content = $conn->query("select mimetype from content where objectid = '" . $objectId . "' order by majorversion desc, minorversion desc limit 1");
                    if ($content) {
                        $json .= ",\"mimetype\":\"" . $content->fetch_object()->mimetype . "\"";
                    }
                }
                $json .= "}";
                $prefix = ",";
            }
            $json .= "]";
            return $json;
        }
        else
            return false;
    }
    finally {
        $conn->close();
    }
   }

   public function updateUser($user) {
    try {
        $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
        $idp = $this->getIdentityProvider($user->getIdentifyProviderId());
        if (is_null($idp->getRolesCache())) {
            $idprolesJson = $idp->getRoles();
            $conn->query("update identityproviders set rolescache = '" . $conn->real_escape_string($idprolesJson) . "' where id = '" . $idp->getId() . "'");
            $idproles = json_decode($idprolesJson);
        }
        else {
            $idproles = json_decode($idp->getRolesCache());
        }
        $roles = $user->getRoles();

        $succeeded = false;

        while (!$succeeded) {
            $roleids = [];
            $foundRoles = true;
            foreach($roles as $role) {
                $roleId = null;
                for ($index = 0; $index < sizeof($idproles); $index++) {
                    if ($idproles[$index]->name == $role) {
                        $roleId = $idproles[$index]->id;
                        break;
                    }
                }
                if (is_null($roleId)) {
                    $foundRoles = false;
                    break;
                }
                else {
                    $roleids[sizeof($roleids)] = $roleId;
                }
            }
            if ($foundRoles)
                $succeeded = true;
            else {
                $idprolesJson = $idp->getRoles();
                $conn->query("update identityproviders set rolescache = '" . $conn->real_escape_string($idprolesJson) . "' where id = '" . $idp->getId() . "'");
                $idproles = json_decode($idprolesJson);
            }
        }
        sort($roleids);
        $roleCache = implode($roleids);
        $cacherows = $conn->query("select roles from usercache where userid = '" . $user->getId() . "' and identityproviderid = '" . $user->getIdentifyProviderId() . "'");
        $refresh = false;
        if ($cacherows && $cacherows->num_rows == 1) {
            $cache = $cacherows->fetch_object();
            if ($cache->roles != $roleCache) {
                $refresh = true;
            }
        }
        else {
            $refresh = true;
        }
        if ($refresh) {
            $conn->query("delete from userrolesgroups where userid = '" . $user->getId() . "' and identityproviderid = '" . $user->getIdentifyProviderId() . "'");
            foreach($roleids as $role) {
                $conn->query("insert into userrolesgroups (userid, identityproviderid, roleid) values ('" . $user->getId() . "','" . $user->getIdentifyProviderId() . "','" . $role . "')");
            }
            if ($cacherows->num_rows == 1) 
                $conn->query("update usercache set roles = '" . $roleCache . "' where userid = '" . $user->getId() . "' and identityproviderid = '" . $user->getIdentifyProviderId() . "'");
            else
                $conn->query("insert into usercache (userid, identityproviderid, roles) values ('" . $user->getId() . "','" . $user->getIdentifyProviderId() . "','" . $roleCache . "')");
        }
    }
    finally {
        $conn->close();
    }
}

public function addRequestPerformance($url, $method, $time) {
    try {
        $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
        $conn->query("insert into requestperformance(url, method, time) values ('" . $url . "','" . $method . "','" . $time . "')");
    }
    finally {
        $conn->close();
    }
}

public function canEditStore($storeid) {
    try {
        $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
        $rights = $conn->query("select level from rights where systemright='Update'");
        if ($rights->num_rows == 1) {
            $checks = $conn->query("select hasStoreRight('" . $storeid . "','" . $this->userId . "','" . $this->identityProviderId . "'," . $rights->fetch_object()->level . ") hasright from rights LIMIT 1");
            if ($checks && $checks->num_rows == 1) {
                return $checks->fetch_object()->hasright == 1;
            }
            else {
                return false;
            }
        }
        else
            return false;
    }
    finally {
        $conn->close();
    }
}

public function updateStore($storeid, $request) {
    try {
        $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
        if ($request->id == $storeid) {
            $conn->begin_transaction();
            $succeeded = true;
    
            //Update rights
            if ($conn->query("delete from grantedrights where targetid = '" . $storeid . "'")) {
                foreach($request->grantedrights as $right) {
                    if ($conn->query("insert into grantedrights (targetid, granteeid, granteetype, identityproviderid, level, weight, targettype) values ('" . $storeid . "','" . $right->grantee . "','" . $right->granteetype . "','" . $right->identityprovider . "'," . $right->level . "," . $this->getGranteeTypeWeigth($right->granteetype) . ",'store')")) {
                        //
                    }
                    else {
                        $succeeded = FALSE;
                    }
                }
            }
            else {
                $succeeded = false;
            }

            //Update name
            if (isset($request->name)) {
                if ($conn->query("update stores set name = '" . $request->name . "' where id = '" . $storeid . "'")) {
                }
                else
                    $succeeded = FALSE;
            }

            if ($succeeded) {
                $conn->commit();
                return true;
            }
            else {
                $conn->rollback();
                return false;
            }
        
        }
        else
            return false;
    }
    finally {
        $conn->close();
    }
}

public function getDomainRights() {
    try {
        $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
        $rights = $conn->query("select granteeid, granteetype, identityproviderid, level, weight from grantedrights where targettype = 'domain'");
        $json = "[";
        while ($right = $rights->fetch_object()) {
            $json .= ($json == "[" ? "" : ".") . (new GrantedRight($right->granteeid, $right->granteetype, $right->identityproviderid, $right->level, $right->weight))->toJson();
        }
        $json .= "]";
        return $json;
    }
    finally {
        $conn->close();
    }
}

public function canGetMimetypes() {
    return $this->canGetDomainRights();
}

public function canGetDomainRights() {
    try {
        $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
        $rights = $conn->query("select level from rights where systemright='read'");
        if ($rights->num_rows == 1) {
            $results = $conn->query("select hasDomainRight('" . $this->userId . "','" . $this->identityProviderId . "'," . $rights->fetch_object()->level . ") hasright from rights LIMIT 1");
            if ($results && $results->num_rows == 1) {
                return $results->fetch_object()->hasright == 1;
            }
            else {
                echo 'Unable to create store';
                return false;
            }
        }
        else {
            return false;
        }
    }
    finally {
        $conn->close();
    }
}

public function getMimetypes() {
    try {
        $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
        $mimetypes = $conn->query("select mimetype, extension, icon, iconfilename from mimetype");
        $json = "[";
        while ($mimetype = $mimetypes->fetch_object()) {
            $json .= ($json == "[" ? "": ",");
            $json .= "{\"mimetype\":\"" . $mimetype->mimetype . "\", \"extension\":\"" . $mimetype->extension . "\"";
            if (!is_null($mimetype->icon)) {
                $json .= ",\"icon\":\"" . base64_encode($mimetype->icon) . "\",\"iconfilename\":\"" . $mimetype->iconfilename . "\"";
            }
            $json .= "}";
        }
        $json .= "]";
        return $json;
    }
    finally {
        $conn->close();
    }
}
public function canCreateMimetype() {
    try {
        $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
        $rights = $conn->query("select level from rights where systemright='createmimetype'");
        if ($rights->num_rows == 1) {
            $results = $conn->query("select hasDomainRight('" . $this->userId . "','" . $this->identityProviderId . "'," . $rights->fetch_object()->level . ") hasright from rights LIMIT 1");
            if ($results && $results->num_rows == 1) {
                return $results->fetch_object()->hasright == 1;
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }
    finally {
        $conn->close();
    }
}

public function createMimetype($request) {
    try {
        $conn = mysqli_connect($this->host, $this->user, $this->password, $this->dbname);
        $stmt = $conn->prepare("insert into mimetype(mimetype,extension,iconfilename,icon) values ('" . $request->mimetype . "','" . $request->extension . "','" . $request->iconfilename . "',?)");
        $data = base64_decode($request->iconcontent);
        $stmt->bind_param('s', $data);
        $stmt->execute();
        echo mysqli_error($conn);
    }
    finally {
        $conn->close();
    }
}

}