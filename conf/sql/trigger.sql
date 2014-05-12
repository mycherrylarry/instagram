create trigger trigger_msg after insert on photo for each row 
insert into msgbox(user_id, followee_id, photo_id) 
(select friendship.follower_id, NEW.contributer, NEW.id from friendship where friendship.followee_id = NEW.contributer);

create trigger trigger_incre after insert on likeship for each row update photo set photo.like_num = photo.like_num + 1
where photo.id = NEW.photo_id;

create trigger trigger_decre after delete on likeship for each row update photo set photo.like_num = photo.like_num - 1
where photo.id = OLD.photo_id;
