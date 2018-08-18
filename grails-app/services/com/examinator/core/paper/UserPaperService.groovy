package com.examinator.core.paper

import com.examinator.core.Paper
import com.examinator.core.Section
import com.examinator.core.UserPaper
import com.examinator.security.authentication.User

import com.mongodb.client.FindIterable
import grails.core.GrailsApplication

import static com.mongodb.client.model.Filters.*
import org.bson.types.ObjectId

import grails.gorm.transactions.Transactional

@Transactional
class UserPaperService {

    def questionPaperService
    GrailsApplication grailsApplication

    UserPaper generateNewPaper(User user, String subject) {
        int grade = user.settings.grade
        Paper paper = questionPaperService.getPaper(grade, subject)
        UserPaper userPaper = new UserPaper([userId: user.id, paper: paper])
        userPaper.save()
        return userPaper
    }

    UserPaper getPaper(String paperId) {
        ObjectId id = new ObjectId(paperId)
        UserPaper paper = UserPaper.find(eq('id',id)).first()
        return paper
    }

    List<UserPaper> getAllPapers(User user){

        FindIterable iterable = UserPaper.collection.find(eq('userId', user.id))

        List<UserPaper> papers = iterable.limit(10).collect {  this.documentToUserPaperDomain( it ) }

        return  papers
    }

    private UserPaper documentToUserPaperDomain(def document){
        UserPaper userPaper = (UserPaper) document
        def documentSections = document["paper"]["sections"]
        List<Section> sections = documentSections.collect { documentSection ->
            String clazzName = documentSection["__thisClazzName__"]
            grailsApplication.getClassForName(clazzName).newInstance(documentSection)
        }
        userPaper.paper.sections = sections
        return userPaper
    }
}
