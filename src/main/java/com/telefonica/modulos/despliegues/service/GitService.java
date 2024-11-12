package com.telefonica.modulos.despliegues.service;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class GitService {

    public List<String> getCommitComparison(String path, String primerCommit, String ultimoCommit) throws Exception {
    List<String> files = new ArrayList<>();
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        Repository repository = builder.setGitDir(new File(path))
                .readEnvironment()
                .findGitDir()
                .build();

        org.eclipse.jgit.api.Git git = new org.eclipse.jgit.api.Git(repository);

        ObjectReader reader = git.getRepository().newObjectReader();
        CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
        ObjectId oldTree = git.getRepository().resolve( primerCommit + "^{tree}" ); // ff90a995bc8f9a67cabbb863775a0f137485b9cc
        oldTreeIter.reset( reader, oldTree );
        CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
        ObjectId newTree = git.getRepository().resolve( ultimoCommit + "^{tree}" ); // cf0dd1d1ed887df852b0291bfc0ef1f539f7a376
        newTreeIter.reset( reader, newTree );

        DiffFormatter df = new DiffFormatter( new ByteArrayOutputStream() ); // use NullOutputStream.INSTANCE if you don't need the diff output
        df.setRepository( git.getRepository() );
        List<DiffEntry> entries = df.scan( oldTreeIter, newTreeIter );
        for( DiffEntry entry : entries ) {
            String activo = entry.toString().replace("DiffEntry[", "").replace("]", "").replace("MODIFY ", "Modificado - ").replace("ADD ", "Nuevo - ");
            files.add(activo);
        }
        return files;
    }
}
